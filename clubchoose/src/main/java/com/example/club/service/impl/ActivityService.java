package com.example.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.entity.Activity;
import com.example.club.entity.ActivityRegistration;
import com.example.club.entity.ActivitySignin;
import com.example.club.entity.User;
import com.example.club.mapper.ActivityMapper;
import com.example.club.mapper.ActivityRegistrationMapper;
import com.example.club.mapper.ActivitySigninMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService extends ServiceImpl<ActivityMapper, Activity> {

    @Resource
    private ActivityRegistrationMapper registrationMapper;
    @Resource
    private ActivitySigninMapper signinMapper;

    public List<Activity> listActivities(Integer clubId) {
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        if (clubId != null) wrapper.eq("club_id", clubId);
        wrapper.orderByDesc("time");
        return this.list(wrapper);
    }

    @Transactional
    public String signActivity(Integer activityId, Integer userId) {
        Activity activity = getById(activityId);
        if (activity == null) return "活动不存在";
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants())
            return "人数已满";
        // 检查是否已报名（只要有记录就不允许重复报名，因为取消报名会物理删除）
        LambdaQueryWrapper<ActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegistration::getActivityId, activityId)
                .eq(ActivityRegistration::getUserId, userId);
        if (registrationMapper.selectCount(wrapper) > 0) {
            return "您已经报名过该活动";
        }
        ActivityRegistration reg = new ActivityRegistration();
        reg.setActivityId(activityId);
        reg.setUserId(userId);
        reg.setSignTime(new Date());
        reg.setIsCancelled(false);
        registrationMapper.insert(reg);
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        updateById(activity);
        return "报名成功";
    }

    @Transactional
    public String cancelSign(Integer activityId, Integer userId) {
        LambdaQueryWrapper<ActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegistration::getActivityId, activityId)
                .eq(ActivityRegistration::getUserId, userId);
        ActivityRegistration reg = registrationMapper.selectOne(wrapper);
        if (reg == null) return "未报名或已取消";
        // 物理删除报名记录，允许重新报名
        registrationMapper.deleteById(reg.getId());
        Activity activity = getById(activityId);
        if (activity != null && activity.getCurrentParticipants() > 0) {
            activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
            updateById(activity);
        }
        return "取消报名成功";
    }

    public String generateSignCode(Integer activityId) {
        String code = DigestUtils.md5DigestAsHex((activityId + "" + System.currentTimeMillis()).getBytes()).substring(0, 8);
        Activity activity = getById(activityId);
        if (activity != null) {
            activity.setSignCode(code);
            updateById(activity);
        }
        return code;
    }

    @Transactional
    public String signIn(Integer activityId, Integer userId, String code) {
        Activity activity = getById(activityId);
        if (activity == null) return "活动不存在";
        if (activity.getSignCode() == null || !activity.getSignCode().equals(code))
            return "签到码错误";
        LambdaQueryWrapper<ActivitySignin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignin::getActivityId, activityId)
                .eq(ActivitySignin::getUserId, userId);
        if (signinMapper.selectCount(wrapper) > 0) return "您已签到过";
        ActivitySignin signin = new ActivitySignin();
        signin.setActivityId(activityId);
        signin.setUserId(userId);
        signin.setSigninTime(new Date());
        signin.setSigninCode(code);
        signinMapper.insert(signin);
        return "签到成功";
    }

    public List<User> getSignUpList(Integer activityId) {
        return registrationMapper.selectSignUpUsers(activityId);
    }
    /**
     * 获取用户已签到的活动ID列表
     */
    public List<Integer> getUserSigninActivityIds(Integer userId) {
        LambdaQueryWrapper<ActivitySignin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignin::getUserId, userId);
        List<ActivitySignin> list = signinMapper.selectList(wrapper);
        return list.stream().map(ActivitySignin::getActivityId).collect(Collectors.toList());
    }
    public List<Activity> getSignedActivitiesByUser(Integer userId) {
        // 查询用户报名且未取消的活动ID
        LambdaQueryWrapper<ActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegistration::getUserId, userId)
                .eq(ActivityRegistration::getIsCancelled, false);
        List<ActivityRegistration> registrations = registrationMapper.selectList(wrapper);
        if (registrations.isEmpty()) return new java.util.ArrayList<>();
        java.util.Set<Integer> activityIds = registrations.stream()
                .map(ActivityRegistration::getActivityId)
                .collect(java.util.stream.Collectors.toSet());
        LambdaQueryWrapper<Activity> activityWrapper = new LambdaQueryWrapper<>();
        activityWrapper.in(Activity::getId, activityIds)
                .orderByDesc(Activity::getTime);
        return this.list(activityWrapper);
    }
}
package com.example.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.club.entity.Activity;
import com.example.club.entity.ActivityRegistration;
import com.example.club.entity.ActivitySignin;
import com.example.club.entity.Notice;
import com.example.club.entity.NoticeRead;
import com.example.club.mapper.ActivityMapper;
import com.example.club.mapper.ActivityRegistrationMapper;
import com.example.club.mapper.ActivitySigninMapper;
import com.example.club.mapper.NoticeMapper;
import com.example.club.mapper.NoticeReadMapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@EnableScheduling
public class ScheduleService {

    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ActivityRegistrationMapper registrationMapper;
    @Resource
    private ActivitySigninMapper signinMapper;
    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private NoticeReadMapper noticeReadMapper;

    // 每天凌晨2点执行清理
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldData() {
        Date sevenDaysAgo = new Date(System.currentTimeMillis() - 7 * 24 * 3600 * 1000);
        // 删除7天前的活动及其关联数据
        List<Activity> oldActivities = activityMapper.selectList(
                new QueryWrapper<Activity>().lt("time", sevenDaysAgo));
        for (Activity activity : oldActivities) {
            registrationMapper.delete(new QueryWrapper<ActivityRegistration>().eq("activity_id", activity.getId()));
            signinMapper.delete(new QueryWrapper<ActivitySignin>().eq("activity_id", activity.getId()));
            activityMapper.deleteById(activity.getId());
        }
        // 删除7天前的通知及其已读记录
        List<Notice> oldNotices = noticeMapper.selectList(
                new QueryWrapper<Notice>().lt("create_time", sevenDaysAgo));
        for (Notice notice : oldNotices) {
            noticeReadMapper.delete(new QueryWrapper<NoticeRead>().eq("notice_id", notice.getId()));
            noticeMapper.deleteById(notice.getId());
        }
    }
}
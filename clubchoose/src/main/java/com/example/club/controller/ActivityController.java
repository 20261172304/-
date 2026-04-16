package com.example.club.controller;

import com.example.club.entity.Activity;
import com.example.club.entity.User;
import com.example.club.service.impl.ActivityService;
import com.example.club.service.impl.ClubMemberService;
import com.example.club.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin
public class ActivityController {

    @Resource
    private ActivityService activityService;
    @Resource
    private ClubMemberService clubMemberService;

    @GetMapping("/list")
    public List<Activity> list(Integer clubId) {
        return activityService.listActivities(clubId);
    }

    @PostMapping("/add")
    public String add(@RequestBody Activity activity, @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return "未登录";
        if (!clubMemberService.isManager(activity.getClubId(), userId))
            return "无权限，仅社团管理员可发布活动";
        activityService.save(activity);
        return "发布成功";
    }

    @PostMapping("/sign")
    public String sign(@RequestBody Map<String, Object> req, @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return "未登录";
        Integer activityId = (Integer) req.get("activityId");
        return activityService.signActivity(activityId, userId);
    }

    @PostMapping("/cancel")
    public String cancel(@RequestBody Map<String, Object> req, @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return "未登录";
        Integer activityId = (Integer) req.get("activityId");
        return activityService.cancelSign(activityId, userId);
    }

    @GetMapping("/sign-list")
    public List<User> signList(@RequestParam Integer activityId) {
        return activityService.getSignUpList(activityId);
    }
    /**
     * 获取用户已签到的活动ID列表
     */
    @GetMapping("/my-signins")
    public List<Integer> mySignins(@RequestParam Integer userId) {
        return activityService.getUserSigninActivityIds(userId);
    }
    @PostMapping("/generate-code")
    public String generateCode(@RequestParam Integer activityId, @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return "未登录";
        Activity activity = activityService.getById(activityId);
        if (!clubMemberService.isManager(activity.getClubId(), userId))
            return "无权限";
        return activityService.generateSignCode(activityId);
    }
    @GetMapping("/my-signed")
    public List<Activity> mySignedActivities(@RequestParam Integer userId) {
        return activityService.getSignedActivitiesByUser(userId);
    }
    @PostMapping("/signin")
    public String signin(@RequestBody Map<String, Object> req, @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return "未登录";
        Integer activityId = (Integer) req.get("activityId");
        String code = (String) req.get("code");
        return activityService.signIn(activityId, userId, code);
    }
}
package com.example.club.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.club.entity.MemberApply;
import com.example.club.service.impl.ClubMemberService;
import com.example.club.service.impl.MemberApplyService;
import com.example.club.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/member-apply")
@CrossOrigin
public class MemberApplyController {

    @Resource
    private MemberApplyService applyService;
    @Resource
    private ClubMemberService memberService;

    @PostMapping("/add")
    public String add(@RequestBody MemberApply apply, @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return "未登录";
        apply.setUserId(userId);
        apply.setStatus("PENDING");
        apply.setCreateTime(new Date());
        applyService.save(apply);
        return "申请已提交";
    }

    @PutMapping("/audit")
    public String audit(@RequestParam Integer applyId, @RequestParam String status,
                        @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        MemberApply apply = applyService.getById(applyId);
        if (apply == null) return "申请不存在";
        if (!memberService.isManager(apply.getClubId(), userId)) {
            return "无权限，仅社团管理员可审核";
        }
        boolean ok = applyService.audit(applyId, status, userId);
        return ok ? "操作成功" : "操作失败";
    }

    @GetMapping("/pending-list")
    public List<MemberApply> pendingList(@RequestParam Integer clubId) {
        QueryWrapper<MemberApply> wrapper = new QueryWrapper<>();
        wrapper.eq("club_id", clubId).eq("status", "PENDING");
        return applyService.list(wrapper);
    }

}
package com.example.club.controller;

import com.example.club.entity.ClubMember;
import com.example.club.entity.User;
import com.example.club.service.impl.ClubMemberService;
import com.example.club.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/club/member")
@CrossOrigin
public class ClubMemberController {

    @Resource
    private ClubMemberService memberService;

    @GetMapping("/list")
    public List<User> list(@RequestParam Integer clubId) {
        return memberService.getMemberUsers(clubId);
    }

    @PutMapping("/role")
    public String setRole(@RequestParam Integer clubId, @RequestParam Integer userId, @RequestParam String role,
                          @RequestHeader("Authorization") String token) {
        Integer operatorId = JwtUtil.getUserIdFromToken(token);
        if (!memberService.isManager(clubId, operatorId)) {
            return "无权限";
        }
        ClubMember member = memberService.getOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ClubMember>()
                .eq("club_id", clubId).eq("user_id", userId));
        if (member == null) return "成员不存在";
        member.setRole(role);
        memberService.updateById(member);
        return "设置成功";
    }

    @DeleteMapping("/remove")
    public String remove(@RequestParam Integer clubId, @RequestParam Integer userId,
                         @RequestHeader("Authorization") String token) {
        Integer operatorId = JwtUtil.getUserIdFromToken(token);
        if (!operatorId.equals(userId) && !memberService.isManager(clubId, operatorId)) {
            return "无权限";
        }
        memberService.remove(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ClubMember>()
                .eq("club_id", clubId).eq("user_id", userId));
        return "移除成功";
    }
}
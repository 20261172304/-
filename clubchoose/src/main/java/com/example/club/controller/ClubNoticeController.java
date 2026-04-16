package com.example.club.controller;

import com.example.club.entity.ClubNotice;
import com.example.club.service.impl.ClubMemberService;
import com.example.club.service.impl.ClubNoticeService;
import com.example.club.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/club/notice")
@CrossOrigin
public class ClubNoticeController {

    @Resource
    private ClubNoticeService clubNoticeService;   // 改名，避免与 NoticeService 的 bean 名称冲突

    @Resource
    private ClubMemberService memberService;

    @GetMapping("/list")
    public List<ClubNotice> list(@RequestParam Integer clubId) {
        return clubNoticeService.listByClub(clubId);
    }

    @PostMapping("/add")
    public String add(@RequestBody ClubNotice notice, @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return "未登录";
        if (!memberService.isManager(notice.getClubId(), userId)) {
            return "无权限，仅社团管理员可发布公告";
        }
        notice.setCreateTime(new Date());
        notice.setIsTop(notice.getIsTop() == null ? false : notice.getIsTop());
        clubNoticeService.save(notice);
        return "发布成功";
    }

    @PutMapping("/top/{id}")
    public String setTop(@PathVariable Integer id, @RequestParam Boolean top, @RequestHeader("Authorization") String token) {
        ClubNotice notice = clubNoticeService.getById(id);
        if (notice == null) return "公告不存在";
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (!memberService.isManager(notice.getClubId(), userId)) {
            return "无权限";
        }
        notice.setIsTop(top);
        clubNoticeService.updateById(notice);
        return "操作成功";
    }
}
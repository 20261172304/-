package com.example.club.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.club.entity.Club;
import com.example.club.entity.ClubMember;
import com.example.club.entity.User;
import com.example.club.service.impl.ClubMemberService;
import com.example.club.service.impl.ClubService;
import com.example.club.service.impl.UserService;
import com.example.club.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/api/club")
@CrossOrigin
public class ClubController {

    @Resource
    private ClubService clubService;
    @Resource
    private UserService userService;
    @Resource
    private ClubMemberService clubMemberService;
    @Value("${file.upload-dir}")
    private String uploadDir;
    @GetMapping("/list")
    public List<Club> list(String name) {
        return clubService.listClubs(name);
    }

    @PostMapping("/apply")
    public String apply(@RequestBody Club club) {
        club.setStatus("PENDING");
        clubService.save(club);
        return "申请已提交";
    }

    @GetMapping("/pending-list")
    public Map<String, Object> pendingList(@RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return Map.of("code", 401, "msg", "未登录");
        User user = userService.getById(userId);
        if (user == null || !"ADMIN".equals(user.getRole()))
            return Map.of("code", 403, "msg", "无权限");
        List<Club> list = clubService.list(new QueryWrapper<Club>().eq("status", "PENDING"));
        return Map.of("code", 200, "data", list);
    }

    @PutMapping("/audit/{id}")
    public Map<String, Object> audit(@PathVariable Integer id, @RequestParam String status,
                                     @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return Map.of("code", 401, "msg", "未登录");
        User admin = userService.getById(userId);
        if (admin == null || !"ADMIN".equals(admin.getRole()))
            return Map.of("code", 403, "msg", "无权限");
        Club club = clubService.getById(id);
        if (club == null) return Map.of("code", 404, "msg", "社团不存在");
        if ("APPROVED".equals(status)) {
            club.setStatus("APPROVED");
            clubService.updateById(club);
            if (club.getLeaderId() != null) {
                ClubMember member = new ClubMember();
                member.setUserId(club.getLeaderId());
                member.setClubId(club.getId());
                member.setRole("MANAGER");
                member.setJoinTime(new Date());
                clubMemberService.save(member);
            }
        } else if ("REJECTED".equals(status)) {
            club.setStatus("REJECTED");
            clubService.updateById(club);
        } else {
            return Map.of("code", 400, "msg", "无效状态");
        }
        return Map.of("code", 200, "msg", "操作成功");
    }

    @PostMapping("/upload-logo/{id}")
    public Map<String, Object> uploadLogo(@PathVariable Integer id,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Map.of("code", 401, "msg", "未登录");
        }
        Club club = clubService.getById(id);
        if (club == null) {
            return Map.of("code", 404, "msg", "社团不存在");
        }
        User user = userService.getById(userId);
        boolean isAdmin = user != null && "ADMIN".equals(user.getRole());
        boolean isManager = clubMemberService.isManager(id, userId);
        if (!isAdmin && !isManager) {
            return Map.of("code", 403, "msg", "无权限，仅社团管理员或超级管理员可上传标志");
        }
        try {
            // 确保上传目录存在（需要在类中注入 @Value("${file.upload-dir}") private String uploadDir;）
            String uploadPath = uploadDir + "/logos/";
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = UUID.randomUUID().toString() + ".jpg";
            File dest = new File(uploadPath + fileName);
            file.transferTo(dest);
            String logoUrl = "/uploads/logos/" + fileName;
            club.setLogo(logoUrl);
            clubService.updateById(club);
            return Map.of("code", 200, "url", logoUrl, "msg", "上传成功");
        } catch (Exception e) {
            return Map.of("code", 500, "msg", "上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/admin/create")
    public Map<String, Object> createClubByAdmin(@RequestBody Club club, @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return Map.of("code", 401, "msg", "未登录");
        User user = userService.getById(userId);
        if (user == null || !"ADMIN".equals(user.getRole()))
            return Map.of("code", 403, "msg", "无权限");
        club.setStatus("APPROVED");
        club.setCreateTime(new Date());
        clubService.save(club);
        if (club.getLeaderId() == null) club.setLeaderId(userId);
        clubService.updateById(club);
        ClubMember member = new ClubMember();
        member.setUserId(userId);
        member.setClubId(club.getId());
        member.setRole("MANAGER");
        member.setJoinTime(new Date());
        clubMemberService.save(member);
        return Map.of("code", 200, "msg", "社团创建成功", "clubId", club.getId());
    }
    @PutMapping("/disable/{id}")
    public Map<String, Object> disableClub(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return Map.of("code", 401, "msg", "未登录");

        Club club = clubService.getById(id);
        if (club == null) return Map.of("code", 404, "msg", "社团不存在");

        User user = userService.getById(userId);
        boolean isAdmin = user != null && "ADMIN".equals(user.getRole());
        boolean isManager = clubMemberService.isManager(id, userId);

        if (!isAdmin && !isManager) {
            return Map.of("code", 403, "msg", "无权限，仅社团管理员或超级管理员可废除社团");
        }

        if ("DISABLED".equals(club.getStatus())) {
            return Map.of("code", 400, "msg", "社团已处于废除状态");
        }

        club.setStatus("DISABLED");
        clubService.updateById(club);
        return Map.of("code", 200, "msg", "社团已废除");
    }
}
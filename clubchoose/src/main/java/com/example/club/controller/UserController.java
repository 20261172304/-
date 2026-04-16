package com.example.club.controller;

import com.example.club.entity.User;
import com.example.club.service.impl.UserService;
import com.example.club.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Resource
    private UserService userService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        return userService.login(user.getUsername(), user.getPassword());
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        user.setRole("STUDENT");
        boolean saved = userService.register(user);
        if (saved) return Map.of("code", 200, "msg", "注册成功");
        return Map.of("code", 500, "msg", "注册失败");
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody User user, @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return Map.of("code", 401, "msg", "未登录");
        User exist = userService.getById(userId);
        if (exist == null) return Map.of("code", 404, "msg", "用户不存在");
        exist.setEmail(user.getEmail());
        if (user.getAvatar() != null) exist.setAvatar(user.getAvatar());
        boolean ok = userService.updateById(exist);
        return Map.of("code", ok ? 200 : 500, "msg", ok ? "更新成功" : "更新失败");
    }

    @PostMapping("/upload-avatar")
    public Map<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) return Map.of("code", 401, "msg", "未登录");
        try {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            String uploadPath = uploadDir + "/";
            File dest = new File(uploadPath + fileName);
            if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
            file.transferTo(dest);
            // 关键修改：去掉 /avatars 层，直接 /uploads/文件名
            String avatarUrl = "/uploads/" + fileName;
            User user = userService.getById(userId);
            user.setAvatar(avatarUrl);
            userService.updateById(user);
            return Map.of("code", 200, "url", avatarUrl);
        } catch (Exception e) {
            return Map.of("code", 500, "msg", e.getMessage());
        }
    }
}
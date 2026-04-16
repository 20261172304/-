package com.example.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.entity.User;
import com.example.club.mapper.UserMapper;
import com.example.club.utils.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Resource
    private ClubMemberService clubMemberService;

    public Map<String, Object> login(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = this.getOne(wrapper);
        Map<String, Object> result = new HashMap<>();
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            List<Integer> managedClubIds = clubMemberService.getManagedClubIds(user.getId());
            user.setManagedClubIds(managedClubIds);
            String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            result.put("token", token);
            result.put("user", user);
            result.put("code", 200);
        } else {
            result.put("code", 500);
            result.put("msg", "用户名或密码错误");
        }
        return result;
    }

    public boolean register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(new Date());
        return this.save(user);
    }
}
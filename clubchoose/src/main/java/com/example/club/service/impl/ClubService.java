package com.example.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.entity.Club;
import com.example.club.mapper.ClubMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClubService extends ServiceImpl<ClubMapper, Club> {
    public List<Club> listClubs(String name) {
        QueryWrapper<Club> wrapper = new QueryWrapper<>();
        if (name != null) wrapper.like("name", name);
        wrapper.eq("status", "APPROVED");
        return this.list(wrapper);
    }
}
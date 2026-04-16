package com.example.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.entity.ClubNotice;
import com.example.club.mapper.ClubNoticeMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClubNoticeService extends ServiceImpl<ClubNoticeMapper, ClubNotice> {
    public List<ClubNotice> listByClub(Integer clubId) {
        QueryWrapper<ClubNotice> wrapper = new QueryWrapper<>();
        wrapper.eq("club_id", clubId)
                .orderByDesc("is_top")
                .orderByDesc("create_time");
        return this.list(wrapper);
    }
}
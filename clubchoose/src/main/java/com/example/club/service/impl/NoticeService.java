package com.example.club.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.entity.Notice;
import com.example.club.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

@Service
public class NoticeService extends ServiceImpl<NoticeMapper, Notice> {
    // 简单的增删改查由 BaseMapper 提供
}
package com.example.club.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.club.entity.Notice;
import com.example.club.entity.NoticeRead;
import com.example.club.mapper.NoticeMapper;
import com.example.club.mapper.NoticeReadMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notice")
@CrossOrigin
public class NoticeController {

    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private NoticeReadMapper noticeReadMapper;

    @GetMapping("/list")
    public List<Notice> list(@RequestParam Integer userId) {
        List<Notice> notices = noticeMapper.selectList(new QueryWrapper<Notice>().orderByDesc("create_time"));
        // 查询已读记录
        List<NoticeRead> reads = noticeReadMapper.selectList(new QueryWrapper<NoticeRead>().eq("user_id", userId));
        Set<Integer> readIds = reads.stream().map(NoticeRead::getNoticeId).collect(Collectors.toSet());
        for (Notice n : notices) {
            n.setRead(readIds.contains(n.getId()));
        }
        return notices;
    }

    @PostMapping("/add")
    public String add(@RequestBody Notice notice) {
        notice.setCreateTime(new Date());
        noticeMapper.insert(notice);
        return "发布成功";
    }

    @PostMapping("/mark-read/{noticeId}")
    public String markRead(@PathVariable Integer noticeId, @RequestParam Integer userId) {
        // 使用 QueryWrapper 查询复合主键
        QueryWrapper<NoticeRead> wrapper = new QueryWrapper<>();
        wrapper.eq("notice_id", noticeId).eq("user_id", userId);
        NoticeRead nr = noticeReadMapper.selectOne(wrapper);
        if (nr == null) {
            nr = new NoticeRead();
            nr.setNoticeId(noticeId);
            nr.setUserId(userId);
            nr.setReadTime(new Date());
            noticeReadMapper.insert(nr);
        }
        return "已读";
    }

    @GetMapping("/unread-count")
    public Integer unreadCount(@RequestParam Integer userId) {
        List<Notice> all = noticeMapper.selectList(null);
        List<NoticeRead> reads = noticeReadMapper.selectList(new QueryWrapper<NoticeRead>().eq("user_id", userId));
        return all.size() - reads.size();
    }
}
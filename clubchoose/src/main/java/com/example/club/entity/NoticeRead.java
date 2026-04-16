package com.example.club.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@TableName("notice_read")
public class NoticeRead {
    private Integer noticeId;
    private Integer userId;
    private Date readTime;

    // getters and setters
    public Integer getNoticeId() { return noticeId; }
    public void setNoticeId(Integer noticeId) { this.noticeId = noticeId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Date getReadTime() { return readTime; }
    public void setReadTime(Date readTime) { this.readTime = readTime; }
}
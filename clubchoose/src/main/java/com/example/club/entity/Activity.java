package com.example.club.entity;

import java.util.Date;

public class Activity {
    private Integer id;
    private String title;
    private String content;
    private Date time;          // 开始时间
    private Date endTime;       // 新增：结束时间
    private Integer clubId;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String signCode;

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Integer getClubId() { return clubId; }
    public void setClubId(Integer clubId) { this.clubId = clubId; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
    public Integer getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(Integer currentParticipants) { this.currentParticipants = currentParticipants; }
    public String getSignCode() { return signCode; }
    public void setSignCode(String signCode) { this.signCode = signCode; }
}
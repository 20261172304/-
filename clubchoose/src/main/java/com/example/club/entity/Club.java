package com.example.club.entity;

import java.util.Date;

public class Club {
    private Integer id;
    private String name;
    private String description;
    private String leader; // 社长
    private Date createTime;
    private String status; // PENDING, APPROVED
    private Integer leaderId;
    private String logo;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLeader() { return leader; }
    public void setLeader(String leader) { this.leader = leader; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getLeaderId() { return leaderId; }
    public void setLeaderId(Integer leaderId) { this.leaderId = leaderId; }
    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }
}
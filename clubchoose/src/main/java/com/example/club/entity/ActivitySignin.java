package com.example.club.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@TableName("activity_signin")
public class ActivitySignin {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer activityId;
    private Integer userId;
    private Date signinTime;
    private String signinCode;

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getActivityId() { return activityId; }
    public void setActivityId(Integer activityId) { this.activityId = activityId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Date getSigninTime() { return signinTime; }
    public void setSigninTime(Date signinTime) { this.signinTime = signinTime; }
    public String getSigninCode() { return signinCode; }
    public void setSigninCode(String signinCode) { this.signinCode = signinCode; }
}
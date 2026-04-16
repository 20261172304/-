package com.example.club.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
@Data
public class Notice {
    private Integer id;
    private String title;
    private String content;
    private Date createTime;
    private Integer adminId;
    @TableField(exist = false)
    private Integer isTop; // 是否置顶 0否 1是
    @TableField(exist = false)
    private Integer type;
    @TableField(exist = false)
    private Boolean read;   // 是否已读，不存数据库

    // getters and setters (原有 + 新)
    public Boolean getRead() { return read; }
    public void setRead(Boolean read) { this.read = read; }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }
}
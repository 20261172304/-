package com.example.club.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import java.util.Date;
import java.util.List;

public class User {
    private Integer id;
    private String username;
    private String password;
    private String role;
    private String email;
    private String avatar;
    private Date createTime;
    @TableField(exist = false)
    private String clubRole;

    @TableField(exist = false)
    private List<Integer> managedClubIds;

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public List<Integer> getManagedClubIds() { return managedClubIds; }
    public void setManagedClubIds(List<Integer> managedClubIds) { this.managedClubIds = managedClubIds; }
    public String getClubRole() { return clubRole; }
    public void setClubRole(String clubRole) { this.clubRole = clubRole; }
}
package com.example.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.entity.ClubMember;
import com.example.club.entity.User;
import com.example.club.mapper.ClubMemberMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubMemberService extends ServiceImpl<ClubMemberMapper, ClubMember> {

    public boolean isManager(Integer clubId, Integer userId) {
        QueryWrapper<ClubMember> wrapper = new QueryWrapper<>();
        wrapper.eq("club_id", clubId).eq("user_id", userId).eq("role", "MANAGER");
        return this.count(wrapper) > 0;
    }

    public boolean isMember(Integer clubId, Integer userId) {
        QueryWrapper<ClubMember> wrapper = new QueryWrapper<>();
        wrapper.eq("club_id", clubId).eq("user_id", userId);
        return this.count(wrapper) > 0;
    }

    public List<User> getMemberUsers(Integer clubId) {
        return baseMapper.selectMemberUsers(clubId);
    }

    public List<Integer> getManagedClubIds(Integer userId) {
        QueryWrapper<ClubMember> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("role", "MANAGER");
        List<ClubMember> list = this.list(wrapper);
        return list.stream().map(ClubMember::getClubId).collect(Collectors.toList());
    }
}
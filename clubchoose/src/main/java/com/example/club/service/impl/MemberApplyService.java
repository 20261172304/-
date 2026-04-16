package com.example.club.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.entity.ClubMember;
import com.example.club.entity.MemberApply;
import com.example.club.mapper.MemberApplyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class MemberApplyService extends ServiceImpl<MemberApplyMapper, MemberApply> {

    @Resource
    private ClubMemberService clubMemberService;

    @Transactional
    public boolean audit(Integer applyId, String status, Integer managerId) {
        MemberApply apply = getById(applyId);
        if (apply == null) return false;
        apply.setStatus(status);
        updateById(apply);
        if ("APPROVED".equals(status)) {
            ClubMember member = new ClubMember();
            member.setUserId(apply.getUserId());
            member.setClubId(apply.getClubId());
            member.setRole("MEMBER");
            member.setJoinTime(new Date());
            clubMemberService.save(member);
        }
        return true;
    }
}
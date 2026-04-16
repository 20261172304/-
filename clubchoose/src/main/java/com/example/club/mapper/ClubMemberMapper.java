package com.example.club.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.club.entity.ClubMember;
import com.example.club.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ClubMemberMapper extends BaseMapper<ClubMember> {
    @Select("SELECT u.*, cm.role AS clubRole FROM club_member cm LEFT JOIN user u ON cm.user_id = u.id WHERE cm.club_id = #{clubId}")
    List<User> selectMemberUsers(Integer clubId);
}
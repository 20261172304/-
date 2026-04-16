package com.example.club.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.club.entity.ActivityRegistration;
import com.example.club.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ActivityRegistrationMapper extends BaseMapper<ActivityRegistration> {
    @Select("SELECT u.* FROM activity_registration ar LEFT JOIN user u ON ar.user_id = u.id WHERE ar.activity_id = #{activityId} AND ar.is_cancelled = 0")
    List<User> selectSignUpUsers(Integer activityId);
}
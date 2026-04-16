package com.example.club.mapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.club.entity.User;
@Mapper
public interface UserMapper extends BaseMapper<User> {}
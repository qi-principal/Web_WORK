package com.web.ads.mapper;

import com.web.ads.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    
    @Insert("INSERT INTO user (username, email, password, user_type, created_at, updated_at) " +
            "VALUES (#{username}, #{email}, #{password}, #{userType}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);

    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User findById(Integer userId);

    @Update("UPDATE user SET username = #{username}, email = #{email}, " +
            "password = #{password}, user_type = #{userType}, updated_at = NOW() " +
            "WHERE user_id = #{userId}")
    int update(User user);

    @Select("SELECT * FROM user")
    List<User> findAll();

} 
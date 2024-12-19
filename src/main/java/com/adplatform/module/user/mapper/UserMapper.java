package com.adplatform.module.user.mapper;

import com.adplatform.module.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 *
 * @author andrew
 * @date 2023-11-21
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
} 
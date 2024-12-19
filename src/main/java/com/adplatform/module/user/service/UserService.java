package com.adplatform.module.user.service;

import com.adplatform.module.user.dto.LoginRequest;
import com.adplatform.module.user.dto.LoginResponse;
import com.adplatform.module.user.dto.RegisterRequest;
import com.adplatform.module.user.dto.UserDTO;

/**
 * 用户服务接口
 *
 * @author andrew
 * @date 2023-11-21
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户信息
     */
    UserDTO register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserDTO getUserById(Long id);

    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);
} 
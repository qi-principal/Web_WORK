package com.adplatform.module.user.controller;

import com.adplatform.common.response.Result;
import com.adplatform.module.user.dto.LoginRequest;
import com.adplatform.module.user.dto.LoginResponse;
import com.adplatform.module.user.dto.RegisterRequest;
import com.adplatform.module.user.dto.UserDTO;
import com.adplatform.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 *
 * @author andrew
 * @date 2023-11-21
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户信息
     */
    @PostMapping("/register")
    public Result<UserDTO> register(@Validated @RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }
} 
package com.adplatform.module.user.controller;

import com.adplatform.common.response.Result;
import com.adplatform.module.user.dto.LoginRequest;
import com.adplatform.module.user.dto.LoginResponse;
import com.adplatform.module.user.dto.RegisterRequest;
import com.adplatform.module.user.dto.UserDTO;
import com.adplatform.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("/v1/auth")
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
        log.info("收到注册请求，用户名: {}, 邮箱: {}", request.getUsername(), request.getEmail());
        Result<UserDTO> result = Result.success(userService.register(request));
        log.info("注册成功，用户ID: {}", result.getData().getId());
        return result;
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        log.info("收到登录请求，用户名: {}", request.getUsername());
        Result<LoginResponse> result = Result.success(userService.login(request));
        log.info("登录成功，用户ID: {}", result.getData().getUser().getId());
        return result;
    }
} 
package com.adplatform.module.user.controller;

import com.adplatform.common.response.Result;
import com.adplatform.module.user.dto.LoginRequest;
import com.adplatform.module.user.dto.LoginResponse;
import com.adplatform.module.user.dto.RegisterRequest;
import com.adplatform.module.user.dto.UserDTO;
import com.adplatform.module.user.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author andrew
 * @date 2023-11-21
 */
@Slf4j
@Api(tags = "认证接口")
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
    @ApiOperation("用户注册")
    @ApiResponses({
        @ApiResponse(code = 200, message = "注册成功"),
        @ApiResponse(code = 400, message = "参数错误"),
        @ApiResponse(code = 409, message = "用户名已存在")
    })
    @PostMapping("/register")
    public Result<UserDTO> register(
        @ApiParam(value = "注册请求参数，包含用户名、密码、邮箱等信息", required = true, name = "registerRequest", example = "{\"username\":\"test\",\"password\":\"123456\",\"email\":\"test@example.com\"}")
        @Validated @RequestBody RegisterRequest request
    ) {
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
    @ApiOperation("用户登录")
    @ApiResponses({
        @ApiResponse(code = 200, message = "登录成功"),
        @ApiResponse(code = 400, message = "参数错误"),
        @ApiResponse(code = 401, message = "用户名或密码错误"),
        @ApiResponse(code = 403, message = "账号已被禁用")
    })
    @PostMapping("/login")
    public Result<LoginResponse> login(
        @ApiParam(value = "登录请求参数，包含用户名、密码", required = true, name = "loginRequest", example = "{\"username\":\"sun\",\"password\":\"123456\"}")
        @Validated @RequestBody LoginRequest request
    ) {
        log.info("收到登录请求，用户名: {}", request.getUsername());
        Result<LoginResponse> result = Result.success(userService.login(request));
        log.info("登录成功，用户ID: {}", result.getData().getUser().getId());
        return result;
    }
} 
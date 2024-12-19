package com.adplatform.module.user.controller;

import com.adplatform.common.response.Result;
import com.adplatform.module.user.dto.UserDTO;
import com.adplatform.module.user.security.UserPrincipal;
import com.adplatform.module.user.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author andrew
 * @date 2023-11-21
 */
@Slf4j
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @ApiOperation("获取用户信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取用户信息成功"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        log.info("接收到获取用户信息请求，用户ID: {}", id);
        UserDTO userDTO = userService.getUserById(id);
        log.info("获取用户信息成功，用户ID: {}", id);
        return Result.success(userDTO);
    }

    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 状态
     * @return 操作结果
     */
    @ApiOperation("更新用户状态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新用户状态成功"),
        @ApiResponse(code = 403, message = "没有权限更新用户状态"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("接收到更新用户状态请求，用户ID: {}, 新状态: {}", id, status);
        userService.updateStatus(id, status);
        log.info("更新用户状态成功，用户ID: {}", id);
        return Result.success();
    }

    /**
     * 获取当前用户信息
     *
     * @param currentUser 当前用户
     * @return 用户信息
     */
    @ApiOperation("获取当前用户信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取当前用户信息成功"),
        @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/me")
    public Result<UserDTO> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("接收到获取当前用户信息请求，用户ID: {}", currentUser.getId());
        UserDTO userDTO = userService.getUserById(currentUser.getId());
        log.info("获取当前用户信息成功，用户ID: {}", currentUser.getId());
        return Result.success(userDTO);
    }
} 
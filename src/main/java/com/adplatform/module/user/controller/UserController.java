package com.adplatform.module.user.controller;

import com.adplatform.common.response.Result;
import com.adplatform.module.user.dto.UserDTO;
import com.adplatform.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author andrew
 * @date 2023-11-21
 */
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
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 状态
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }
} 
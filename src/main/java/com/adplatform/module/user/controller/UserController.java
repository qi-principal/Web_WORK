package com.adplatform.module.user.controller;

import com.adplatform.common.response.Result;
import com.adplatform.module.user.dto.UserDTO;
import com.adplatform.module.user.dto.UserStatisticsDTO;
import com.adplatform.module.user.dto.UserLogDTO;
import com.adplatform.module.user.dto.UserLoginHistoryDTO;
import com.adplatform.module.user.dto.UpdateUserProfileRequest;
import com.adplatform.module.user.dto.UserNotificationDTO;
import com.adplatform.module.user.dto.UserSecuritySettingDTO;
import com.adplatform.module.user.dto.UserFeedbackDTO;
import com.adplatform.module.user.security.UserPrincipal;
import com.adplatform.module.user.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author andrew
 * @date 2023-11-21
 */
@Slf4j
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/v1/users")
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

    /**
     * 获取用户列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户列表
     */
    @ApiOperation("获取用户列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取用户列表成功"),
        @ApiResponse(code = 403, message = "没有权限获取用户列表")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<UserDTO>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("接收到获取用户列表请求，页码: {}, 每页数量: {}", pageNum, pageSize);
        Page<UserDTO> userPage = userService.getUserList(pageNum, pageSize);
        log.info("获取用户列表成功，总记录数: {}", userPage.getTotalElements());
        return Result.success(userPage);
    }

    /**
     * 获取用户统计信息
     */
    @ApiOperation("获取用户统计信息")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserStatisticsDTO> getUserStatistics() {
        log.info("接收到获取用户统计信息请求");
        UserStatisticsDTO statistics = userService.getUserStatistics();
        log.info("获取用户统计信息成功");
        return Result.success(statistics);
    }

    /**
     * 获取最近注册用户
     */
    @ApiOperation("获取最近注册用户")
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<UserDTO>> getRecentUsers(
            @RequestParam(defaultValue = "7") Integer days,
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("接收到获取最近注册用户请求，天数: {}, 限制: {}", days, limit);
        List<UserDTO> users = userService.getRecentUsers(days, limit);
        log.info("获取最近注册用户成功，返回 {} 条记录", users.size());
        return Result.success(users);
    }

    /**
     * 更新用户余额
     */
    @ApiOperation("更新用户余额")
    @PutMapping("/{id}/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateBalance(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        log.info("接收到更新用户余额请求，用户ID: {}, 金额: {}", id, amount);
        userService.updateUserBalance(id, amount);
        log.info("更新用户余额成功");
        return Result.success();
    }

    /**
     * 搜索用户
     */
    @ApiOperation("搜索用户")
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<UserDTO>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(userService.searchUsers(keyword, pageNum, pageSize));
    }

    /**
     * 导出用户数据
     */
    @ApiOperation("导出用户数据")
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportUserData() {
        byte[] data = userService.exportUserData();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "users.xlsx");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    /**
     * 获取用户操作日志
     */
    @ApiOperation("获取用户操作日志")
    @GetMapping("/{id}/logs")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public Result<Page<UserLogDTO>> getUserLogs(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(userService.getUserLogs(id, pageNum, pageSize));
    }

    /**
     * 更新用户详细信息
     */
    @ApiOperation("更新用户详细信息")
    @PutMapping("/{id}/profile")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public Result<UserDTO> updateUserProfile(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserProfileRequest request) {
        return Result.success(userService.updateUserProfile(id, request));
    }

    /**
     * 获取用户登录历史
     */
    @ApiOperation("获取用户登录历史")
    @GetMapping("/{id}/login-history")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public Result<List<UserLoginHistoryDTO>> getUserLoginHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(userService.getUserLoginHistory(id, limit));
    }

    /**
     * 强制用户下线
     */
    @ApiOperation("强制用户下线")
    @PostMapping("/{id}/force-logout")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> forceLogout(@PathVariable Long id) {
        userService.forceLogout(id);
        return Result.success();
    }

    /**
     * 设置用户标签
     */
    @ApiOperation("设置用户标签")
    @PutMapping("/{id}/tags")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> setUserTags(
            @PathVariable Long id,
            @RequestBody List<String> tags) {
        userService.setUserTags(id, tags);
        return Result.success();
    }

    /**
     * 获取用户通知
     */
    @ApiOperation("获取用户通知")
    @GetMapping("/{id}/notifications")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public Result<Page<UserNotificationDTO>> getUserNotifications(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(userService.getUserNotifications(id, pageNum, pageSize));
    }

    /**
     * 标记通知为已读
     */
    @ApiOperation("标记通知为已读")
    @PutMapping("/{userId}/notifications/{notificationId}/read")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#userId)")
    public Result<Void> markNotificationAsRead(
            @PathVariable Long userId,
            @PathVariable Long notificationId) {
        userService.markNotificationAsRead(userId, notificationId);
        return Result.success();
    }

    /**
     * 获取用户安全设置
     */
    @ApiOperation("获取用户安全设置")
    @GetMapping("/{id}/security-settings")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public Result<UserSecuritySettingDTO> getUserSecuritySettings(@PathVariable Long id) {
        return Result.success(userService.getUserSecuritySettings(id));
    }

    /**
     * 更新用户安全设置
     */
    @ApiOperation("更新用户安全设置")
    @PutMapping("/{id}/security-settings")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public Result<Void> updateUserSecuritySettings(
            @PathVariable Long id,
            @RequestBody UserSecuritySettingDTO settings) {
        userService.updateUserSecuritySettings(id, settings);
        return Result.success();
    }

    /**
     * 提交用户反馈
     */
    @ApiOperation("提交用户反馈")
    @PostMapping("/{id}/feedback")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public Result<Void> submitFeedback(
            @PathVariable Long id,
            @RequestBody UserFeedbackDTO feedback) {
        userService.submitFeedback(id, feedback);
        return Result.success();
    }

    /**
     * 获取用户活跃度统计
     */
    @ApiOperation("获取用户活跃度统计")
    @GetMapping("/{id}/activity-stats")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public Result<Map<String, Object>> getUserActivityStats(@PathVariable Long id) {
        return Result.success(userService.getUserActivityStats(id));
    }
} 
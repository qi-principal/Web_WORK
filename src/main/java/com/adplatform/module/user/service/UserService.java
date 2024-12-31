package com.adplatform.module.user.service;

import com.adplatform.module.user.dto.LoginRequest;
import com.adplatform.module.user.dto.LoginResponse;
import com.adplatform.module.user.dto.RegisterRequest;
import com.adplatform.module.user.dto.UserDTO;
import com.adplatform.module.user.dto.UserStatisticsDTO;
import com.adplatform.module.user.dto.UserLogDTO;
import com.adplatform.module.user.dto.UserLoginHistoryDTO;
import com.adplatform.module.user.dto.UserTransactionDTO;
import com.adplatform.module.user.dto.UpdateUserProfileRequest;
import com.adplatform.module.user.dto.UserNotificationDTO;
import com.adplatform.module.user.dto.UserSecuritySettingDTO;
import com.adplatform.module.user.dto.UserFeedbackDTO;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取用户列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页用户列表
     */
    Page<UserDTO> getUserList(Integer pageNum, Integer pageSize);

    /**
     * 根据用户类型获取用户列表
     */
    List<UserDTO> getUsersByType(Integer userType);

    /**
     * 更新用户余额
     */
    void updateUserBalance(Long userId, BigDecimal amount);

    /**
     * 获取用户统计信息
     */
    UserStatisticsDTO getUserStatistics();

    /**
     * 获取最近注册的用户
     */
    List<UserDTO> getRecentUsers(Integer days, Integer limit);

    /**
     * 获取不活跃用户
     */
    List<UserDTO> getInactiveUsers(Integer days);

    /**
     * 批量更新用户状态
     */
    void batchUpdateStatus(List<Long> userIds, Integer status);

    /**
     * 根据余额范围查询用户
     */
    List<UserDTO> getUsersByBalanceRange(BigDecimal minBalance, BigDecimal maxBalance);

    /**
     * 检查用户名是否可用
     */
    boolean isUsernameAvailable(String username);

    /**
     * 重置用户密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 搜索用户
     */
    Page<UserDTO> searchUsers(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 导出用户数据
     */
    byte[] exportUserData();

    /**
     * 批量导入用户
     */
    void importUsers(List<UserDTO> users);

    /**
     * 获取用户操作日志
     */
    Page<UserLogDTO> getUserLogs(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 更新用户详细信息
     */
    UserDTO updateUserProfile(Long userId, UpdateUserProfileRequest request);

    /**
     * 获取用户登录历史
     */
    List<UserLoginHistoryDTO> getUserLoginHistory(Long userId, Integer limit);

    /**
     * 强制用户下线
     */
    void forceLogout(Long userId);

    /**
     * 获取用户消费记录
     */
    Page<UserTransactionDTO> getUserTransactions(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 设置用户标签
     */
    void setUserTags(Long userId, List<String> tags);

    /**
     * 根据标签查询用户
     */
    List<UserDTO> getUsersByTags(List<String> tags);

    /**
     * 获取用户通知
     */
    Page<UserNotificationDTO> getUserNotifications(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 标记通知为已读
     */
    void markNotificationAsRead(Long userId, Long notificationId);

    /**
     * 获取用户安全设置
     */
    UserSecuritySettingDTO getUserSecuritySettings(Long userId);

    /**
     * 更新用户安全设置
     */
    void updateUserSecuritySettings(Long userId, UserSecuritySettingDTO settings);

    /**
     * 启用两步验证
     */
    void enableTwoFactorAuth(Long userId, String type);

    /**
     * 验证两步验证码
     */
    boolean verifyTwoFactorCode(Long userId, String code);

    /**
     * 提交用户反馈
     */
    void submitFeedback(Long userId, UserFeedbackDTO feedback);

    /**
     * 获取用户反馈列表
     */
    Page<UserFeedbackDTO> getUserFeedback(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 回复用户反馈
     */
    void replyToFeedback(Long feedbackId, String adminReply);

    /**
     * 获取用户活跃度统计
     */
    Map<String, Object> getUserActivityStats(Long userId);

    /**
     * 获取用户推荐
     */
    List<UserDTO> getRecommendedUsers(Long userId, Integer limit);

    /**
     * 检查用户密码强度
     */
    Map<String, Object> checkPasswordStrength(String password);

    /**
     * 生成用户访问令牌
     */
    String generateAccessToken(Long userId, Integer expiryDays);

    /**
     * 吊销用户访问令牌
     */
    void revokeAccessToken(Long userId, String tokenId);

    /**
     * 获取用户API使用统计
     */
    Map<String, Object> getUserApiUsageStats(Long userId);
} 
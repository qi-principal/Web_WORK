package com.adplatform.module.user.repository;

import com.adplatform.module.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户数据访问接口
 *
 * @author andrew
 * @date 2023-11-21
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {
    
    /**
     * 根据用户名查找用户
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查找用户
     */
    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(@Param("email") String email);
    
    /**
     * 根据手机号查找用户
     */
    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(@Param("phone") String phone);
    
    /**
     * 根据用户类型查找用户列表
     */
    @Select("SELECT * FROM user WHERE user_type = #{userType}")
    List<User> findByUserType(@Param("userType") Integer userType);
    
    /**
     * 更新用户余额
     */
    @Update("UPDATE user SET balance = balance + #{amount}, update_time = NOW() WHERE id = #{userId}")
    int updateBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
    
    /**
     * 查找指定余额以上的用户
     */
    @Select("SELECT * FROM user WHERE balance >= #{minBalance}")
    List<User> findByBalanceGreaterThanEqual(@Param("minBalance") BigDecimal minBalance);
    
    /**
     * 统计各用户类型的数量
     */
    @Select("SELECT user_type, COUNT(*) as count FROM user GROUP BY user_type")
    List<UserTypeCount> countByUserType();
    
    /**
     * 查找最近注册的用户
     */
    @Select("SELECT * FROM user WHERE create_time >= #{days} ORDER BY create_time DESC LIMIT #{limit}")
    List<User> findRecentUsers(@Param("days") String days, @Param("limit") Integer limit);
    
    /**
     * 查找长期未登录的用户
     */
    @Select("SELECT * FROM user WHERE update_time < #{date}")
    List<User> findInactiveUsers(@Param("date") String date);
    
    /**
     * 搜索用户
     */
    @Select("SELECT * FROM user WHERE " +
            "username LIKE CONCAT('%', #{keyword}, '%') OR " +
            "email LIKE CONCAT('%', #{keyword}, '%') OR " +
            "phone LIKE CONCAT('%', #{keyword}, '%')")
    List<User> searchUsers(@Param("keyword") String keyword);
    
    /**
     * 记录用户操作日志
     */
    @Insert("INSERT INTO user_log (user_id, operation, description, ip_address, user_agent, operation_time) " +
            "VALUES (#{userId}, #{operation}, #{description}, #{ipAddress}, #{userAgent}, NOW())")
    void insertUserLog(@Param("userId") Long userId, 
                      @Param("operation") String operation,
                      @Param("description") String description,
                      @Param("ipAddress") String ipAddress,
                      @Param("userAgent") String userAgent);
    
    /**
     * 获取用户操作日志
     */
    @Select("SELECT * FROM user_log WHERE user_id = #{userId} ORDER BY operation_time DESC LIMIT #{offset}, #{limit}")
    List<UserLog> getUserLogs(@Param("userId") Long userId, 
                             @Param("offset") Integer offset, 
                             @Param("limit") Integer limit);
    
    /**
     * 记录用户登录历史
     */
    @Insert("INSERT INTO user_login_history (user_id, ip_address, location, device_info, login_time, login_success, fail_reason) " +
            "VALUES (#{userId}, #{ipAddress}, #{location}, #{deviceInfo}, NOW(), #{loginSuccess}, #{failReason})")
    void insertLoginHistory(@Param("userId") Long userId,
                          @Param("ipAddress") String ipAddress,
                          @Param("location") String location,
                          @Param("deviceInfo") String deviceInfo,
                          @Param("loginSuccess") Boolean loginSuccess,
                          @Param("failReason") String failReason);
    
    /**
     * 获取用户登录历史
     */
    @Select("SELECT * FROM user_login_history WHERE user_id = #{userId} ORDER BY login_time DESC LIMIT #{limit}")
    List<UserLoginHistory> getUserLoginHistory(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * 获取用户消费记录
     */
    @Select("SELECT * FROM user_transaction WHERE user_id = #{userId} ORDER BY transaction_time DESC LIMIT #{offset}, #{limit}")
    List<UserTransaction> getUserTransactions(@Param("userId") Long userId,
                                            @Param("offset") Integer offset,
                                            @Param("limit") Integer limit);
    
    /**
     * 设置用户标签
     */
    @Insert("INSERT INTO user_tag (user_id, tag_name) VALUES (#{userId}, #{tagName})")
    void insertUserTag(@Param("userId") Long userId, @Param("tagName") String tagName);
    
    /**
     * 删除用户标签
     */
    @Delete("DELETE FROM user_tag WHERE user_id = #{userId}")
    void deleteUserTags(@Param("userId") Long userId);
    
    /**
     * 根据标签查询用户
     */
    @Select("SELECT DISTINCT u.* FROM user u " +
            "INNER JOIN user_tag ut ON u.id = ut.user_id " +
            "WHERE ut.tag_name IN " +
            "<foreach collection='tags' item='tag' open='(' separator=',' close=')'>" +
            "#{tag}" +
            "</foreach>")
    List<User> getUsersByTags(@Param("tags") List<String> tags);
    
    /**
     * 获取用户通知
     */
    @Select("SELECT * FROM user_notification WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<UserNotification> getUserNotifications(@Param("userId") Long userId,
                                              @Param("offset") Integer offset,
                                              @Param("limit") Integer limit);
    
    /**
     * 标记通知为已读
     */
    @Update("UPDATE user_notification SET is_read = true WHERE id = #{notificationId} AND user_id = #{userId}")
    void markNotificationAsRead(@Param("userId") Long userId, @Param("notificationId") Long notificationId);
    
    /**
     * 获取用户安全设置
     */
    @Select("SELECT * FROM user_security_setting WHERE user_id = #{userId}")
    UserSecuritySetting getUserSecuritySettings(@Param("userId") Long userId);
    
    /**
     * 获取用户反馈
     */
    @Select("SELECT * FROM user_feedback WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<UserFeedback> getUserFeedback(@Param("userId") Long userId,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);
    
    /**
     * 获取用户活跃度数据
     */
    @Select("SELECT DATE(login_time) as date, COUNT(*) as count FROM user_login_history " +
            "WHERE user_id = #{userId} AND login_time >= #{startDate} " +
            "GROUP BY DATE(login_time)")
    List<Map<String, Object>> getUserActivityData(@Param("userId") Long userId,
                                                @Param("startDate") LocalDateTime startDate);
    
    /**
     * 获取相似用户推荐
     */
    @Select("SELECT DISTINCT u.* FROM user u " +
            "INNER JOIN user_tag ut1 ON u.id = ut1.user_id " +
            "INNER JOIN user_tag ut2 ON ut1.tag_name = ut2.tag_name " +
            "WHERE ut2.user_id = #{userId} AND u.id != #{userId} " +
            "LIMIT #{limit}")
    List<User> getRecommendedUsers(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * 记录API调用
     */
    @Insert("INSERT INTO user_api_usage (user_id, api_name, request_time, response_time, status) " +
            "VALUES (#{userId}, #{apiName}, #{requestTime}, #{responseTime}, #{status})")
    void logApiUsage(@Param("userId") Long userId,
                     @Param("apiName") String apiName,
                     @Param("requestTime") LocalDateTime requestTime,
                     @Param("responseTime") LocalDateTime responseTime,
                     @Param("status") String status);
} 
package com.adplatform.module.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户角色关联实体类
 *
 * @author andrew
 * @date 2023-11-21
 */
@Data
@TableName("user_role")
public class UserRole {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 角色ID
     */
    private Long roleId;
} 
package com.adplatform.module.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应对象
 *
 * @author andrew
 * @date 2023-11-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * JWT令牌
     */
    private String token;
    
    /**
     * 用户信息
     */
    private UserDTO user;
} 
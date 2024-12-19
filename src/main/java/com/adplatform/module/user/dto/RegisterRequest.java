package com.adplatform.module.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 注册请求对象
 *
 * @author andrew
 * @date 2023-11-21
 */
@Data
public class RegisterRequest {
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 用户类型：1-广告主 2-网站主
     */
    @NotNull(message = "用户类型不能为空")
    private Integer userType;
} 
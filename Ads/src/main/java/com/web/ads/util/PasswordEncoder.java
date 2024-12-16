package com.web.ads.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class PasswordEncoder {
    
    public static String encode(String password) {
        try {
            // 生成盐值
            String salt = UUID.randomUUID().toString().substring(0, 8);
            // 将密码和盐值组合
            String saltedPassword = password + salt;
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            
            // 将hash后的字节数组转为Base64字符串
            String hashedPassword = Base64.getEncoder().encodeToString(hashedBytes);
            // 返回格式: "盐值:加密后的密码"
            return salt + ":" + hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            // 分离盐值和加密后的��码
            String[] parts = encodedPassword.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            String salt = parts[0];
            String hashedPassword = parts[1];
            
            // 使用相同的盐值加密原始密码
            String saltedPassword = rawPassword + salt;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            String newHashedPassword = Base64.getEncoder().encodeToString(hashedBytes);
            
            // 比较两个加密后的密码是否相同
            return hashedPassword.equals(newHashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码验证失败", e);
        }
    }
} 
package com.adplatform.test;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BCrypt加密解密测试类
 * 
 * @author andrew
 * @date 2023-12-21
 */
public class BCryptTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 测试密码加密
     */
    @Test
    public void testEncrypt() {
        String rawPassword = "123456";
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后的密码: " + encodedPassword);
        
        // 验证加密后的密码不等于原始密码
        assertNotEquals(rawPassword, encodedPassword);
        
        // 验证每次加密的结果都不相同
        String anotherEncodedPassword = encoder.encode(rawPassword);
        assertNotEquals(encodedPassword, anotherEncodedPassword);
    }

    /**
     * 测试密码匹配
     */
    @Test
    public void testMatches() {
        String rawPassword = "123456";
        String encodedPassword = encoder.encode(rawPassword);
        
        // 验证原始密码能够匹配加密后的密码
        assertTrue(encoder.matches(rawPassword, encodedPassword));
        
        // 验证错误的密码不能匹配
        assertFalse(encoder.matches("wrong_password", encodedPassword));
    }

    /**
     * 测试不同密码的加密结果
     */
    @Test
    public void testDifferentPasswords() {
        String[] passwords = {
            "123456",
            "admin123",
            "test@123",
            "password123",
            "abc123"
        };

        System.out.println("不同密码的加密结果：");
        for (String password : passwords) {
            String encoded = encoder.encode(password);
            System.out.println(String.format("原始密码: %-15s | 加密结果: %s", password, encoded));
            
            // 验证每个密码都能正确匹配
            assertTrue(encoder.matches(password, encoded));
        }
    }

    /**
     * 测试密码强度
     */
    @Test
    public void testPasswordStrength() {
        // 测试不同长度和复杂度的密码
        String[] passwords = {
            "a",            // 单个字符
            "123",         // 纯数字
            "abc",         // 纯小写字母
            "ABC",         // 纯大写字母
            "abc123",      // 字母数字组合
            "Abc123!@#",   // 包含特殊字符
            "ThisIsAVeryLongPassword123!@#" // 长密码
        };

        System.out.println("不同强度密码的加密结果：");
        for (String password : passwords) {
            long startTime = System.nanoTime();
            String encoded = encoder.encode(password);
            long endTime = System.nanoTime();
            
            System.out.println(String.format(
                "密码: %-30s | 长度: %-3d | 加密时间: %-8.3f ms | 加密结果: %s",
                password,
                password.length(),
                (endTime - startTime) / 1_000_000.0,
                encoded
            ));
            
            // 验证加密结果
            assertTrue(encoder.matches(password, encoded));
        }
    }

    /**
     * 测试BCrypt的工作因子
     */
    @Test
    public void testWorkFactor() {
        String password = "test123";
        
        System.out.println("不同工作因子的性能比较：");
        for (int strength = 4; strength <= 14; strength++) {
            BCryptPasswordEncoder customEncoder = new BCryptPasswordEncoder(strength);
            
            long startTime = System.nanoTime();
            String encoded = customEncoder.encode(password);
            long endTime = System.nanoTime();
            
            System.out.println(String.format(
                "工作因子: %-2d | 加密时间: %-8.3f ms | 加密结果: %s",
                strength,
                (endTime - startTime) / 1_000_000.0,
                encoded
            ));
            
            // 验证加密结果
            assertTrue(customEncoder.matches(password, encoded));
        }
    }
} 
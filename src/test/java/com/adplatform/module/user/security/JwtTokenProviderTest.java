package com.adplatform.module.user.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT令牌工具测试类
 *
 * @author andrew
 * @date 2023-11-21
 */
@SpringBootTest
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Test
    public void testGenerateToken() {
        // 准备测试数据
        UserDetails userDetails = new User("test_user", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 生成token
        String token = tokenProvider.generateToken(authentication);

        // 验证结果
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testGetUsernameFromToken() {
        // 准备测试数据
        UserDetails userDetails = new User("test_user", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 生成token
        String token = tokenProvider.generateToken(authentication);

        // 从token中获取用户名
        String username = tokenProvider.getUsernameFromToken(token);

        // 验证结果
        assertEquals("test_user", username);
    }

    @Test
    public void testValidateToken() {
        // 准备测试数据
        UserDetails userDetails = new User("test_user", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 生成token
        String token = tokenProvider.generateToken(authentication);

        // 验证token
        boolean isValid = tokenProvider.validateToken(token);

        // 验证结果
        assertTrue(isValid);
    }

    @Test
    public void testValidateInvalidToken() {
        // 准备无效的token
        String invalidToken = "invalid.token.string";

        // 验证token
        boolean isValid = tokenProvider.validateToken(invalidToken);

        // 验证结果
        assertFalse(isValid);
    }
} 
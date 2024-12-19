package com.adplatform.module.user.config;

import com.adplatform.module.user.security.UserDetailsServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 测试安全配置类
 *
 * @author andrew
 * @date 2023-11-21
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean("testUserDetailsService")
    @Primary
    public UserDetailsService userDetailsService(UserDetailsServiceImpl userDetailsService) {
        return userDetailsService;
    }
} 
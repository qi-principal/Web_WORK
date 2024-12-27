package com.adserver.config;

import com.adserver.model.User;
import com.adserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestDataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public void initializeTestData() {
        // 创建测试管理员用户
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setAccountType(User.AccountType.ADMIN);
        userRepository.save(adminUser);

        // 创建测试普通用户
        User standardUser = new User();
        standardUser.setUsername("user");
        standardUser.setPassword(passwordEncoder.encode("user123"));
        standardUser.setAccountType(User.AccountType.STANDARD);
        userRepository.save(standardUser);
    }
} 
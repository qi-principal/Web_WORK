package com.adplatform.module.user.service;

import com.adplatform.module.user.entity.User;
import com.adplatform.module.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理员账号初始化服务
 *
 * @author andrew
 * @date 2023-12-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminInitService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 初始化管理员账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void initAdminAccount() {
        // 检查管理员账号是否已存在
        User existingAdmin = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, "admin1")
                .eq(User::getUserType, 3)
        );

        if (existingAdmin != null) {
            log.info("管理员账号已存在，无需初始化");
            return;
        }

        // 创建管理员账号
        User admin = new User();
        admin.setUsername("admin1");
        admin.setPassword(passwordEncoder.encode("123456")); // 使用BCrypt加密密码
        admin.setEmail("admin@adplatform.com");
        admin.setUserType(3); // 3表示管理员
        admin.setStatus(1);   // 1表示正常状态
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());

        try {
            userMapper.insert(admin);
            log.info("管理员账号初始化成功");
        } catch (Exception e) {
            log.error("管理员账号初始化失败", e);
            throw e;
        }
    }
} 
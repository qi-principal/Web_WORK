package com.adplatform.module.user.config;

import com.adplatform.module.user.service.AdminInitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * 管理员账号初始化配置
 *
 * @author andrew
 * @date 2023-12-20
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final AdminInitService adminInitService;

    @Override
    public void run(String... args) {
        try {
            log.info("开始初始化管理员账号...");
            adminInitService.initAdminAccount();
            log.info("管理员账号初始化完成");
        } catch (Exception e) {
            log.error("初始化管理员账号失败", e);
        }
    }
} 
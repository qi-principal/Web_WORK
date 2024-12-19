package com.adplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 广告投放平台启动类
 *
 * @author andrew
 * @date 2023-11-21
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.adplatform.module.*.mapper")
public class AdPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdPlatformApplication.class, args);
    }
} 
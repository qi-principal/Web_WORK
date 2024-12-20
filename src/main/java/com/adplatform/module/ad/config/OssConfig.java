package com.adplatform.module.ad.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置类
 *
 * @author andrew
 * @date 2023-12-19
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssConfig {

    /**
     * endpoint地址
     */
    private String endpoint = "oss-cn-shanghai.aliyuncs.com"; // 配置endpoint

    /**
     * accessKeyId
     */
    private String accessKeyId = "LTAI5tEYmdwCYYmnFua1jaMz"; // 配置accessKeyId

    /**
     * accessKeySecret
     */
    private String accessKeySecret = "3H5QBpDZyUpM1uHqP03RO0GvvHewJu"; // 配置accessKeySecret

    /**
     * bucket名称
     */
    private String bucketName = "web-andrew"; // 配置bucketName

    /**
     * 访问域名
     */
    private String domain = "https://web-andrew.oss-cn-shanghai.aliyuncs.com"; // 配置domain

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
} 
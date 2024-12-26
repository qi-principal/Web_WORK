package org.example.demo.Entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ClassName: AliOss
 * Package: org.example.demo.Entity
 * Description:
 *
 * @Author 谢依雯
 * @Create 2024/12/4 16:26
 */
@Data
@ConfigurationProperties(prefix = "alioss")
@Component
public class AliOss {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
}

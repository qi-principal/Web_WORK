package com.adplatform.module.user.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class UserLogDTO {
    private Long id;
    private Long userId;
    private String operation;
    private String description;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime operationTime;
} 
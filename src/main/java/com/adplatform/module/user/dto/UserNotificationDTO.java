package com.adplatform.module.user.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class UserNotificationDTO {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type;  // SYSTEM, TRANSACTION, SECURITY
    private Boolean isRead;
    private LocalDateTime createTime;
} 
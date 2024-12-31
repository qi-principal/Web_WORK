package com.adplatform.module.user.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class UserFeedbackDTO {
    private Long id;
    private Long userId;
    private String type;  // BUG, FEATURE, COMPLAINT
    private String title;
    private String content;
    private String status;  // PENDING, PROCESSING, RESOLVED, REJECTED
    private String adminReply;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 
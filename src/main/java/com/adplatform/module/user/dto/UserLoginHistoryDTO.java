package com.adplatform.module.user.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class UserLoginHistoryDTO {
    private Long id;
    private Long userId;
    private String ipAddress;
    private String location;
    private String deviceInfo;
    private LocalDateTime loginTime;
    private Boolean loginSuccess;
    private String failReason;
} 
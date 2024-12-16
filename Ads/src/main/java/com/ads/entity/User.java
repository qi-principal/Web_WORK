package com.ads.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Integer userId;
    private String username;
    private String email;
    private String password;
    private String userType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
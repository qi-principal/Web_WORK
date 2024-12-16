package com.web.ads.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Advertisement {
    private Integer adId;
    private String title;
    private String description;
    private String targetUrl;
    private String adImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
package com.web.ads.dto;

import lombok.Data;

/**
 * 广告任务记录DTO
 */
@Data
public class AdTaskRecordDTO {
    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * 记录类型：DISPLAY-展示，CLICK-点击
     */
    private String recordType;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 用户代理
     */
    private String userAgent;
} 
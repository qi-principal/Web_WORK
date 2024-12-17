package com.web.ads.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 广告任务记录实体
 */
@Data
public class AdTaskRecord {
    /**
     * 记录ID
     */
    private Integer recordId;

    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * 记录类型：DISPLAY-展示，CLICK-点击
     */
    private String recordType;

    /**
     * 记录时间
     */
    private LocalDateTime recordTime;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 用户代理
     */
    private String userAgent;
}
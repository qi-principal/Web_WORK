package com.web.ads.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动广告任务DTO
 */
@Data
public class ActiveAdTaskDTO {
    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * 广告ID
     */
    private Integer adId;

    /**
     * 广告标题
     */
    private String adTitle;

    /**
     * 广告图片URL
     */
    private String imageUrl;

    /**
     * 广告链接
     */
    private String adLink;

    /**
     * 任务状态
     */
    private String taskStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 优先级（基于创建时间计算）
     */
    private Integer priority;
} 
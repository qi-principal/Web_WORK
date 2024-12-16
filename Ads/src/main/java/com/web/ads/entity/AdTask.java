package com.web.ads.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 广告任务实体类
 */
@Data
public class AdTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Integer taskId;
    
    /** 目标网站ID */
    private Integer websiteId;
    
    /** 广告位信息 */
    private String adSlotId;
    
    /** 任务状态 */
    private String taskStatus;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
} 
package com.web.ads.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 广告位实体类
 *
 * @author andrew
 * @date 2024/12/16
 */
@Data
public class AdSlot {
    /**
     * 广告位唯一标识
     */
    private Integer adSlotId;

    /**
     * 网站ID
     */
    private Integer websiteId;
    
    /**
     * 广告位类别（如 banner、sidebar 等）
     */
    private String slotType;
    
    /**
     * 广告位尺寸
     */
    private String slotSize;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 
package com.adplatform.module.delivery.dto.response;

import com.adplatform.module.delivery.enums.DeliveryStatus;
import lombok.Data;
import java.util.Date;

/**
 * 广告投放任务响应DTO
 */
@Data
public class DeliveryTaskResponse {
    
    /**
     * 任务ID
     */
    private Long id;

    /**
     * 广告ID
     */
    private Long adId;

    /**
     * 广告位ID
     */
    private Long adSpaceId;

    /**
     * 调度时间
     */
    private Date scheduleTime;

    /**
     * 状态
     */
    private DeliveryStatus status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
} 
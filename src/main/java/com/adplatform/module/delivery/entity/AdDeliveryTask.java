package com.adplatform.module.delivery.entity;

import com.adplatform.module.delivery.enums.DeliveryStatus;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 广告投放任务实体
 */
@Data
@TableName("ad_delivery_task")
public class AdDeliveryTask {
    
    @TableId(type = IdType.AUTO)
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
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 优先级：数字越大优先级越高
     */
    private Integer priority;

    /**
     * 状态：0-待投放、1-投放中、2-已完成、3-已暂停
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    public DeliveryStatus getDeliveryStatus() {
        return DeliveryStatus.fromCode(this.status);
    }

    public void setDeliveryStatus(DeliveryStatus status) {
        this.status = status.getCode();
    }
} 
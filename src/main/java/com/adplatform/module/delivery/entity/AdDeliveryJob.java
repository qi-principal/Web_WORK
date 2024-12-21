package com.adplatform.module.delivery.entity;

import com.adplatform.module.delivery.enums.DeliveryStatus;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 广告投放作业实体
 */
@Data
@TableName("ad_delivery_job")
public class AdDeliveryJob {
    
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的投放任务ID
     */
    private Long taskId;

    /**
     * 展示页面ID
     */
    private Long displayPageId;

    /**
     * 状态：0-待执行、1-执行中、2-已完成、3-失败
     */
    private Integer status;

    /**
     * 执行结果
     */
    private String result;

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
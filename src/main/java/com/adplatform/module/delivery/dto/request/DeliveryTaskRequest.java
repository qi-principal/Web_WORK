package com.adplatform.module.delivery.dto.request;

import lombok.Data;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 广告投放任务请求DTO
 */
@Data
public class DeliveryTaskRequest {
    
    /**
     * 广告ID
     */
    @NotNull(message = "广告ID不能为空")
    private Long adId;

    /**
     * 广告位ID
     */
    @NotNull(message = "广告位ID不能为空")
    private Long adSpaceId;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须是未来时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @Future(message = "结束时间必须是未来时间")
    private Date endTime;

    /**
     * 优先级：数字越大优先级越高
     */
    @NotNull(message = "优先级不能为空")
    @Min(value = 0, message = "优先级必须大于等于0")
    private Integer priority;
} 
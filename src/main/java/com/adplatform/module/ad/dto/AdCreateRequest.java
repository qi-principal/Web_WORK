package com.adplatform.module.ad.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 广告创建请求DTO
 *
 * @author andrew
 * @date 2023-12-19
 */
@Data
public class AdCreateRequest {

    /**
     * 广告标题
     */
    @NotBlank(message = "广告标题不能为空")
    @Size(max = 100, message = "广告标题最多100个字符")
    private String title;

    /**
     * 广告描述
     */
    @Size(max = 500, message = "广告描述最多500个字符")
    private String description;

    /**
     * 广告类型：1-图片 2-视频 3-文字
     */
    @NotNull(message = "广告类型不能为空")
    @Min(value = 1, message = "广告类型不正确")
    @Max(value = 3, message = "广告类型不正确")
    private Integer type;

    /**
     * 总预算
     */
    @NotNull(message = "总预算不能为空")
    @DecimalMin(value = "0.01", message = "总预算必须大于0")
    private BigDecimal budget;

    /**
     * 日预算
     */
    @NotNull(message = "日预算不能为空")
    @DecimalMin(value = "0.01", message = "日预算必须大于0")
    private BigDecimal dailyBudget;

    /**
     * 投放开始时间
     */
    @NotNull(message = "投放开始时间不能为空")
    @Future(message = "投放开始时间必须是将来时间")
    private LocalDateTime startTime;

    /**
     * 投放结束时间
     */
    @NotNull(message = "投放结束时间不能为空")
    @Future(message = "投放结束时间必须是将来时间")
    private LocalDateTime endTime;

    /**
     * 广告素材列表
     */
    private List<MaterialDTO> materials;
}
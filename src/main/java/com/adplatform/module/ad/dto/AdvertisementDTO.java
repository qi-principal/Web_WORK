package com.adplatform.module.ad.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 广告数据传输对象
 *
 * @author andrew
 * @date 2023-12-19
 */
@Data
public class AdvertisementDTO {
    /**
     * 广告ID
     */
    private Long id;

    /**
     * 广告标题
     */
    private String title;

    /**
     * 广告描述
     */
    private String description;

    /**
     * 广告主ID
     */
    private Long userId;

    /**
     * 广告类型：1-图片 2-视频 3-文字
     */
    private Integer type;

    /**
     * 广告类型名称
     */
    private String typeName;

    /**
     * 广告状态：0-草稿 1-待审核 2-审核中 3-已拒绝 4-已通过 5-投放中 6-已暂停 7-已完成
     */
    private Integer status;

    /**
     * 广告状态名称
     */
    private String statusName;

    /**
     * 总预算
     */
    private BigDecimal budget;

    /**
     * 日预算
     */
    private BigDecimal dailyBudget;

    /**
     * 投放开始时间
     */
    private LocalDateTime startTime;

    /**
     * 投放结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 广告素材列表
     */
    private List<MaterialDTO> materials;
} 
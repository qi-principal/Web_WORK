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
    private Long id;
    private String title;
    private String description;
    private Long userId;
    private Integer type;
    private String typeName;
    private Integer status;
    private String statusName;
    private BigDecimal budget;
    private BigDecimal dailyBudget;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<MaterialDTO> materials;
} 
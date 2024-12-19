package com.adplatform.module.ad.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 广告素材数据传输对象
 *
 * @author andrew
 * @date 2023-12-19
 */
@Data
public class MaterialDTO {
    private Long id;
    private Long adId;
    private Integer type;
    private String typeName;
    private String content;
    private String url;
    private Long size;
    private LocalDateTime createTime;
} 
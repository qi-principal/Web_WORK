package com.web.ads.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 网站信息实体类
 *
 * @author andrew
 * @date 2024/12/16
 */
@Data
public class Website {
    /**
     * 网站唯一标识
     */
    private Integer websiteId;
    
    /**
     * 网站名称
     */
    private String websiteName;
    
    /**
     * 网站URL
     */
    private String websiteUrl;
    
    /**
     * 网站介绍
     */
    private String websiteDesc;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 
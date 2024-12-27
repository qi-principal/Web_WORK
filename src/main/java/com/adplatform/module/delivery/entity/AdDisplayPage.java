package com.adplatform.module.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 广告展示页面实体
 */
@Data
@TableName("ad_display_page")
public class AdDisplayPage {
    
    /**
     * 页面ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 广告ID
     */
    private Long adId;

    /**
     * 唯一路径
     */
    private String uniquePath;

    /**
     * 页面URL
     */
    private String url;

    /**
     * 状态：0-无效 1-有效
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
} 
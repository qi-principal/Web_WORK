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
    
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 广告位ID
     */
    private Long adSpaceId;

    /**
     * 唯一路径
     */
    private String uniquePath;

    /**
     * 广告展示页面URL
     */
    private String url;

    /**
     * 当前展示的广告ID
     */
    private Long currentAdId;

    /**
     * 状态：0-禁用、1-启用
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
} 
package com.adplatform.module.ad.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 广告素材实体类
 *
 * @author andrew
 * @date 2023-12-19
 */
@Data
@TableName("ad_material")
public class Material {
    
    /**
     * 素材ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 素材类型：1-图片 2-视频 3-文字
     */
    private Integer type;
    
    /**
     * 素材内容
     */
    private String content;
    
    /**
     * 素材URL
     */
    private String url;
    
    /**
     * 素材大小（字节）
     */
    private Long size;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 
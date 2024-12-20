package com.adplatform.module.ad.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 广告素材关联实体类
 *
 * @author andrew
 * @date 2023-12-19
 */
@Data
@TableName("ad_material_relation")
public class AdMaterialRelation {
    
    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 广告ID
     */
    private Long adId;
    
    /**
     * 素材ID
     */
    private Long materialId;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 
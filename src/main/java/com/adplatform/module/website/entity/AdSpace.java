package com.adplatform.module.website.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("ad_space")
public class AdSpace {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long websiteId;
    private String name;
    private Integer width;
    private Integer height;
    private String code;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
} 
package com.adplatform.module.website.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("website")
public class Website {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String name;
    private String url;
    private String description;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
} 
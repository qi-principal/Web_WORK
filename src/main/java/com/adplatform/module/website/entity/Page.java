package com.adplatform.module.website.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("page")
public class Page {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adSpaceId;
    private String url;
    private String content;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
} 
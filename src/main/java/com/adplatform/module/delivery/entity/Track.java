package com.adplatform.module.delivery.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Track {
    private Integer trackId;
    private String cookieValue;
    private String goodsUrl;
    private String goodsName;// goodsName没用，请忽略
    private LocalDateTime visitDate;
}


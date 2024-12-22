package com.web.ads.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Track {
    private Integer trackId;
    private String cookieValue;
    private String goodsUrl;
    private String goodsName;
    private LocalDateTime visitDate;
}


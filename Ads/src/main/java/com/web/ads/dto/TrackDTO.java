package com.web.ads.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrackDTO {
    private String cookieValue;
    private String goodsUrl;
    private LocalDateTime visitDate;
}


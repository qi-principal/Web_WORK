package com.web.ads.dto;

import lombok.Data;
import java.util.List;

@Data
public class TrackingDTO {
    private String deviceId;
    private List<TrackingItem> items;

    @Data
    public static class TrackingItem {
        private String name;
        private Integer quantity;
    }
} 
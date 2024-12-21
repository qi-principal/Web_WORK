package com.adplatform.module.delivery.enums;

import lombok.Getter;

/**
 * 投放状态枚举
 */
@Getter
public enum DeliveryStatus {
    
    PENDING(0, "待投放"),
    RUNNING(1, "投放中"),
    COMPLETED(2, "已完成"),
    PAUSED(3, "已暂停");

    private final int code;
    private final String description;

    DeliveryStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DeliveryStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DeliveryStatus status : DeliveryStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown delivery status code: " + code);
    }
} 
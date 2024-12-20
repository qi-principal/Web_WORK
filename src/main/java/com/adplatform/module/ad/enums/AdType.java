package com.adplatform.module.ad.enums;

import lombok.Getter;

/**
 * 广告类型枚举
 *
 * @author andrew
 * @date 2023-12-19
 */
@Getter
public enum AdType {
    
    IMAGE(1, "图片广告"),
    VIDEO(2, "视频广告"),
    TEXT(3, "文字广告");

    private final Integer code;
    private final String name;

    AdType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AdType type : values()) {
            if (type.getCode().equals(code)) {
                return type.getName();
            }
        }
        return null;
    }

    public static AdType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AdType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
} 
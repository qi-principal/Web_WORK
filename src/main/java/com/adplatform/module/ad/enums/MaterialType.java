package com.adplatform.module.ad.enums;

import lombok.Getter;

/**
 * 素材类型枚举
 *
 * @author andrew
 * @date 2023-12-19
 */
@Getter
public enum MaterialType {
    IMAGE(1, "图片"),
    VIDEO(2, "视频"),
    TEXT(3, "文字");

    private final Integer code;
    private final String name;

    MaterialType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MaterialType type : values()) {
            if (type.getCode().equals(code)) {
                return type.getName();
            }
        }
        return null;
    }

    public static MaterialType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MaterialType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
} 
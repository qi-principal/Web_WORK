package com.adplatform.module.ad.enums;

import lombok.Getter;

/**
 * 广告状态枚举
 *
 * @author andrew
 * @date 2023-12-19
 */
@Getter
public enum AdStatus {
    DRAFT(0, "草稿"),
    PENDING_REVIEW(1, "待审核"),
    REVIEWING(2, "审核中"),
    REJECTED(3, "已拒绝"),
    APPROVED(4, "已通过"),
    RUNNING(5, "投放中"),
    PAUSED(6, "已暂停"),
    FINISHED(7, "已完成");

    private final Integer code;
    private final String name;

    AdStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AdStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status.getName();
            }
        }
        return null;
    }

    public static AdStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AdStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
} 
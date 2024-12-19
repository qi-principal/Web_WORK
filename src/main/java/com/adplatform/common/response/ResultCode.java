package com.adplatform.common.response;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author andrew
 * @date 2023-11-21
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 不支持的请求方法
     */
    METHOD_NOT_ALLOWED(405, "不支持的请求方法"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 提示信息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
} 
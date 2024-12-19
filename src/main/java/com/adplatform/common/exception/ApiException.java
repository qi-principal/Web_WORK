package com.adplatform.common.exception;

import lombok.Getter;

/**
 * API异常类
 *
 * @author andrew
 * @date 2023-11-21
 */
@Getter
public class ApiException extends RuntimeException {

    private final Integer code;

    public ApiException(String message) {
        super(message);
        this.code = 500;
    }

    public ApiException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public ApiException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
} 
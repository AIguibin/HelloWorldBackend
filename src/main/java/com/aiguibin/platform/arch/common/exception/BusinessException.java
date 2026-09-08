package com.aiguibin.platform.arch.common.exception;

/**
 * 业务异常：由全局异常处理器统一转换为 ResultVO 返回.
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

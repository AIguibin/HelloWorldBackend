package com.aiguibin.platform.arch.dto;

import lombok.Data;

/**
 * API响应结果
 */
@Data
public class Result<T> {

    private boolean success;
    private String message;
    private T data;

    private Result() {
    }

    private Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(true, "操作成功", null);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(true, message, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, message, data);
    }

    public static <T> Result<T> fail() {
        return new Result<>(false, "操作失败", null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(false, message, null);
    }

    public static <T> Result<T> fail(String message, T data) {
        return new Result<>(false, message, data);
    }
}

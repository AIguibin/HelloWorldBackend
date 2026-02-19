package com.aiguibin.platform.arch.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ResultVO<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    private String traceId;
    
    public ResultVO() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public ResultVO(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public ResultVO(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
    
    public static <T> ResultVO<T> success() {
        return new ResultVO<>(200, "操作成功");
    }
    
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(200, "操作成功", data);
    }
    
    public static <T> ResultVO<T> success(String message, T data) {
        return new ResultVO<>(200, message, data);
    }
    
    public static <T> ResultVO<T> error(Integer code, String message) {
        return new ResultVO<>(code, message);
    }
    
    public static <T> ResultVO<T> error(String message) {
        return new ResultVO<>(500, message);
    }
    
    public static <T> ResultVO<T> error(Integer code, String message, T data) {
        return new ResultVO<>(code, message, data);
    }
}
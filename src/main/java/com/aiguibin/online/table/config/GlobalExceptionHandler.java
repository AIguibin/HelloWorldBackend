package com.aiguibin.online.table.config;

import com.aiguibin.online.table.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidation(MethodArgumentNotValidException e) {
        logger.error("程序异常:", e);
        String msg = e.getBindingResult().getFieldError() != null ? e.getBindingResult().getFieldError().getDefaultMessage() : "参数校验失败";
        return ApiResponse.error(msg);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleAny(Exception e) {
        logger.error("程序异常:", e);
        return ApiResponse.error(e.getMessage());
    }
}
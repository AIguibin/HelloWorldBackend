package com.aiguibin.platform.arch.common.exception;

import com.aiguibin.platform.arch.common.result.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * 全局异常处理：统一转换为 ResultVO 结构返回.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResultVO<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResultVO.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<Void> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(f -> f.getDefaultMessage())
                .orElse("参数校验失败");
        return ResultVO.error(400, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultVO<Void> handleUnreadable(HttpMessageNotReadableException e) {
        return ResultVO.error(400, "请求体格式错误");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResultVO<Void> handleDuplicateKey(DuplicateKeyException e) {
        return ResultVO.error(400, "唯一约束冲突");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResultVO<Void> handleResponseStatus(ResponseStatusException e) {
        return ResultVO.error(e.getStatusCode().value(), e.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResultVO<Void> handleUnexpected(Exception e) {
        log.error("系统异常", e);
        return ResultVO.error(500, "系统异常，请稍后重试");
    }
}

package com.aiguibin.platform.arch.common.exception;

import com.aiguibin.platform.arch.common.result.ResultCode;
import com.aiguibin.platform.arch.common.result.ResultVO;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理：统一转换为 ResultVO 结构返回.
 * 覆盖：业务异常、参数校验（body/参数/路径变量）、报文不可读、缺参、类型不匹配、
 * 唯一约束、资源不存在、方法不支持与未捕获异常。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResultVO<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResultVO.error(e.getCode(), e.getMessage());
    }

    /** @RequestBody 上的 @Valid 校验失败. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<Void> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(f -> f.getDefaultMessage())
                .orElse(ResultCode.BAD_REQUEST.getMessage());
        return ResultVO.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    /** @Validated 参数/路径变量上的约束校验失败. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResultVO<Void> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse(ResultCode.BAD_REQUEST.getMessage());
        return ResultVO.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultVO<Void> handleMissingParameter(MissingServletRequestParameterException e) {
        return ResultVO.error(ResultCode.BAD_REQUEST.getCode(), "缺少必填参数: " + e.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResultVO<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResultVO.error(ResultCode.BAD_REQUEST.getCode(), "参数类型错误: " + e.getName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultVO<Void> handleUnreadable(HttpMessageNotReadableException e) {
        return ResultVO.error(ResultCode.BAD_REQUEST.getCode(), "请求体格式错误");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResultVO<Void> handleDuplicateKey(DuplicateKeyException e) {
        return ResultVO.error(ResultCode.BAD_REQUEST.getCode(), "唯一约束冲突");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResultVO<Void> handleNoResource(NoResourceFoundException e) {
        return ResultVO.error(ResultCode.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultVO<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return ResultVO.error(ResultCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResultVO<Void> handleUnexpected(Exception e) {
        log.error("系统异常", e);
        return ResultVO.error(ResultCode.ERROR);
    }
}

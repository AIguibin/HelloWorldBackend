package com.aiguibin.platform.arch.config;

import com.aiguibin.platform.arch.dto.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<?> handleValidation(MethodArgumentNotValidException e) {
        // #region agent log
        try {
            ObjectMapper mapper = new ObjectMapper();
            org.springframework.validation.FieldError fieldError = e.getBindingResult().getFieldError();
            String logEntry = mapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
                put("id", "log_" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8));
                put("timestamp", System.currentTimeMillis());
                put("location", "GlobalExceptionHandler.java:14");
                put("message", "验证失败异常捕获");
                put("data", new java.util.HashMap<String, Object>() {{
                    if (fieldError != null) {
                        put("fieldName", fieldError.getField());
                        put("rejectedValue", fieldError.getRejectedValue());
                        put("defaultMessage", fieldError.getDefaultMessage());
                        put("objectName", fieldError.getObjectName());
                    }
                    put("allErrorsCount", e.getBindingResult().getAllErrors().size());
                }});
                put("sessionId", "debug-session");
                put("runId", "run1");
                put("hypothesisId", "E");
            }});
            Files.write(Paths.get("e:\\WorkSpace\\HelloWorldBackend\\aiguibin-platform-arch\\.cursor\\debug.log"), 
                (logEntry + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ex) {}
        // #endregion
        logger.error("程序异常:", e);
        String msg = e.getBindingResult().getFieldError() != null ? e.getBindingResult().getFieldError().getDefaultMessage() : "参数校验失败";
        return ResultVO.error(msg);
    }

    @ExceptionHandler(Exception.class)
    public ResultVO<?> handleAny(Exception e) {
        logger.error("程序异常:", e);
        return ResultVO.error(e.getMessage());
    }
}
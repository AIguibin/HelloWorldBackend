package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String operator;
    private String operationType;
    private String objectType;
    private Long objectId;
    private String result; // OK/FAIL
    private String message;
    private LocalDateTime operationTime;
    // 新增字段：页面路径、按钮名称、IP地址
    private String pagePath;
    private String buttonName;
    private String ipAddress;
}
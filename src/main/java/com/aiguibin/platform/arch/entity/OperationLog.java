package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志表实体类
 * 对应operation_log表，用于记录系统操作日志
 */
@Data
@TableName("operation_log")
public class OperationLog {
    /**
     * 自增ID，仅做序号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作用户
     */
    private String operator;
    
    /**
     * 操作类型
     */
    private String operationType;
    
    /**
     * 操作对象类型
     */
    private String objectType;
    
    /**
     * 操作对象ID
     */
    private Long objectId;
    
    /**
     * 操作结果：OK/FAIL
     */
    private String result;
    
    /**
     * 操作消息
     */
    private String message;
    
    /**
     * 操作时间
     */
    private LocalDateTime operationTime;
    
    /**
     * 页面路径
     */
    private String pagePath;
    
    /**
     * 按钮名称
     */
    private String buttonName;
    
    /**
     * 操作IP地址
     */
    private String ipAddress;
}
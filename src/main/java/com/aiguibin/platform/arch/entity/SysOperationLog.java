package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志表实体类
 * 对应sys_operation_log表，用于记录系统操作日志
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLog {
    /**
     * UUID，32位随机字符串
     */
    private String uuid;
    
    /**
     * 自增ID，仅做序号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作用户编号
     */
    private String operatorNum;
    
    /**
     * 操作用户姓名
     */
    private String operatorName;
    
    /**
     * 操作类型：CREATE/UPDATE/DELETE/APPROVE/REJECT
     */
    private String operationType;
    
    /**
     * 操作对象类型：biz_change_record等
     */
    private String objectType;
    
    /**
     * 模块名称
     */
    private String module;
    
    /**
     * 操作对象ID
     */
    private Long objectId;
    
    /**
     * 操作对象编码
     */
    private String objectCode;
    
    /**
     * 操作结果：SUCCESS/FAIL
     */
    private String result;
    
    /**
     * 操作描述或失败原因
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
    
    /**
     * 用户代理信息
     */
    private String userAgent;
    
    /**
     * 请求参数
     */
    private String requestParams;
    
    /**
     * 响应数据
     */
    private String responseData;
    
    /**
     * 操作耗时（毫秒）
     */
    private Integer durationMs;
    
    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;
}
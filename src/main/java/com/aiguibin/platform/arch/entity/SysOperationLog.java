package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class SysOperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String operatorNum; // 操作用户编号
    private String operatorName; // 操作用户姓名
    private String operationType; // 操作类型：CREATE/UPDATE/DELETE/APPROVE/REJECT
    private String objectType; // 操作对象类型：biz_change_record等
    private Long objectId; // 操作对象ID
    private String objectCode; // 操作对象编码
    private String result; // 操作结果：OK/FAIL
    private String message; // 操作描述或失败原因
    private LocalDateTime operationTime; // 操作时间
    private String pagePath; // 页面路径
    private String buttonName; // 按钮名称
    private String ipAddress; // 操作IP地址
    private String browserInfo; // 浏览器信息
    private String osInfo; // 操作系统信息

    @TableField(fill = FieldFill.INSERT)
    private String createdBy; // 创建人用户编号
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    private Integer isDeleted; // 是否删除：0-否，1-是
}
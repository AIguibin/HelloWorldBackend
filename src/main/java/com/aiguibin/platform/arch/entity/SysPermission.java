package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一权限定义表实体类
 * 对应sys_permission表，用于存储统一权限定义信息
 */
@Data
@TableName("sys_permission")
public class SysPermission {
    /**
     * UUID，32位随机字符串
     */
    private String uuid;

    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 权限编码，PERM+16位数字
     */
    private String permCode;

    /**
     * 权限名称
     */
    private String permName;

    /**
     * 权限标识（唯一业务标识）
     */
    private String permKey;

    /**
     * 权限类型：1-访问控制，2-数据范围，3-字段控制，4-时间控制，5-业务规则
     */
    private Integer permType;

    /**
     * 操作类型：VIEW-查看，CREATE-新增，UPDATE-修改，DELETE-删除，EXECUTE-执行
     */
    private String actionType;

    /**
     * 生效类型：1-允许，2-禁止
     */
    private Integer effectType;

    /**
     * 条件表达式（JSON格式）
     */
    private String conditionExpression;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 权限描述
     */
    private String description;

    /**
     * 创建人用户编号
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人用户编号
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;
}
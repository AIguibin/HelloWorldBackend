package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字段权限子表实体类
 * 对应sys_field_permission表，用于存储字段级权限配置
 */
@Data
@TableName("sys_field_permission")
public class SysFieldPermission {
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
     * 关联权限编码
     */
    private String permCode;

    /**
     * 字段编码，15位
     */
    private String fieldCode;

    /**
     * 业务实体类型
     */
    private String entityType;

    /**
     * 字段名称（数据库字段名）
     */
    private String fieldName;

    /**
     * 字段别名（显示名称）
     */
    private String fieldAlias;

    /**
     * 字段类型：1-可见，2-可编辑，3-必填，4-隐藏，5-只读
     */
    private Integer fieldType;

    /**
     * 条件表达式（JSON格式，满足条件时生效）
     */
    private String conditionExpression;

    /**
     * 字段默认值
     */
    private String defaultValue;

    /**
     * 验证规则
     */
    private String validationRules;

    /**
     * UI配置
     */
    private String uiConfig;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 字段权限描述
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
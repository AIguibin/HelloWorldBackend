package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据权限子表实体类
 * 对应sys_data_permission表，用于存储数据权限规则
 */
@Data
@TableName("sys_data_permission")
public class SysDataPermission {
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
     * 数据权限名称
     */
    private String dataName;

    /**
     * 业务实体类型（如：biz_change_record）
     */
    private String entityType;

    /**
     * 范围类型：1-全部，2-本机构，3-本部门，4-本人，5-自定义
     */
    private Integer scopeType;

    /**
     * 是否包含下级：0-否，1-是（针对机构、部门）
     */
    private Integer includeChildren;

    /**
     * 规则类型：1-预定义规则，2-自定义SQL，3-组合规则
     */
    private Integer ruleType;

    /**
     * 自定义SQL条件（WHERE子句内容）
     */
    private String customSql;

    /**
     * 组合规则表达式（JSON格式）
     */
    private String ruleExpression;

    /**
     * 规则优先级：1-高，2-中，3-低
     */
    private Integer rulePriority;

    /**
     * 条件字段配置
     */
    private String conditionFields;

    /**
     * 是否全局规则：0-否，1-是
     */
    private Integer isGlobal;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 规则描述
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
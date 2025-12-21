package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 业务权限子表实体类
 * 对应sys_biz_permission表，用于存储业务权限规则
 */
@Data
@TableName("sys_biz_permission")
public class SysBizPermission {
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
     * 业务权限名称
     */
    private String businessName;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务规则（JSON格式）
     */
    private String businessRule;

    /**
     * 规则引擎：DROOLS/EASY_RULES/SPEL
     */
    private String ruleEngine;

    /**
     * 规则优先级
     */
    private Integer rulePriority;

    /**
     * 条件表达式
     */
    private String conditionExpression;

    /**
     * 动作表达式
     */
    private String actionExpression;

    /**
     * 生效类型：1-允许，2-禁止，3-限制
     */
    private Integer effectType;

    /**
     * 限制配置
     */
    private String limitConfig;

    /**
     * 是否审计：0-否，1-是
     */
    private Integer auditEnabled;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 业务权限描述
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
package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限-资源关联表实体类
 * 对应sys_perm_resource表，用于存储权限与资源的关联关系
 */
@Data
@TableName("sys_perm_resource")
public class SysPermResource {
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
     * 权限编码
     */
    private String permCode;

    /**
     * 资源类型：MENU-菜单，API-接口，DATA-数据实体，FIELD-数据字段，TIME-时间规则，BUSINESS-业务规则
     */
    private String resourceType;

    /**
     * 资源标识
     */
    private String resourceKey;

    /**
     * 资源子类型（如：BUTTON-按钮，PAGE-页面）
     */
    private String resourceSubType;

    /**
     * 关联类型：1-主关联，2-条件关联，3-扩展关联
     */
    private Integer relationType;

    /**
     * 关联条件（JSON格式）
     */
    private String conditionExpression;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 关联描述
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
package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色权限关联表实体类
 * 对应sys_role_permission表，用于存储角色与权限的关联关系
 */
@Data
@TableName("sys_role_permission")
public class SysRolePermission {

    /**
     * 自增ID，仅做序号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 权限编码
     */
    private String permCode;

    /**
     * 授权类型：1-允许，2-拒绝
     */
    private Integer authType;

    /**
     * 生效开始时间
     */
    private LocalDateTime effectiveStart;

    /**
     * 生效结束时间
     */
    private LocalDateTime effectiveEnd;

    /**
     * 优先级（数字越小优先级越高）
     */
    private Integer priority;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 描述
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
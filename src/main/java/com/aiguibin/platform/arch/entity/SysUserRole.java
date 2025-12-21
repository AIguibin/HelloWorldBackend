package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关联表实体类
 * 对应sys_user_role表，用于存储用户与角色的关联关系
 */
@Data
@TableName("sys_user_role")
public class SysUserRole {
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
     * 用户编号
     */
    private String userNum;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 是否主角色：0-否，1-是（一个用户只有一个主角色）
     */
    private Integer isPrimary;

    /**
     * 生效开始时间
     */
    private LocalDateTime effectiveStart;

    /**
     * 生效结束时间
     */
    private LocalDateTime effectiveEnd;

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
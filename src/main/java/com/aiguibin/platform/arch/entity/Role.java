package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色表实体类
 * 对应sys_role表，用于存储角色信息
 */
@Data
@TableName("sys_role")
public class Role {
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
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色类型：1-系统角色，2-业务角色
     */
    private Integer roleType;
    
    /**
     * 数据权限范围：1-全部，2-本机构，3-本部门，4-本人，5-自定义
     */
    @TableField("data_scope_type")
    private Integer dataScopeType;
    
    /**
     * 排序号
     */
    private Integer sortOrder;
    
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
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    /**
     * 更新人用户编号
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
    
    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;
}


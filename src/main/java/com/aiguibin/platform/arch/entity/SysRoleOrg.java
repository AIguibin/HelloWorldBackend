package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色机构关联表实体类
 * 对应sys_role_org表，用于存储角色与机构的关联关系
 */
@Data
@TableName("sys_role_org")
public class SysRoleOrg {
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
     * 机构编码
     */
    private String orgCode;
    
    /**
     * 机构范围类型：1-本机构，2-包含下级机构
     */
    @TableField("org_range_type")
    private Integer orgRangeType;
    
    /**
     * 权限类型：1-管理，2-查看，3-操作
     */
    @TableField("perm_type")
    private Integer permType;
    
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
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    /**
     * 更新人用户编号
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;
}
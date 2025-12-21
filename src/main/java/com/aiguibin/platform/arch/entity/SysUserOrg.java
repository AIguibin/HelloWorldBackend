package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户机构关联表实体类
 * 对应sys_user_org表，用于存储用户与机构的关联关系
 */
@Data
@TableName("sys_user_org")
public class SysUserOrg {
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
     * 机构编码
     */
    private String orgCode;
    
    /**
     * 机构名称
     */
    private String orgName;
    
    /**
     * 是否主机构：0-否，1-是
     */
    @TableField("is_primary")
    private Integer isPrimary;
    
    /**
     * 用户在机构中的职位
     */
    private String position;
    
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
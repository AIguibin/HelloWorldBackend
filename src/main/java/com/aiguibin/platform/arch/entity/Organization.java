package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 组织表实体类
 * 对应sys_organization表，用于存储组织信息
 */
@Data
@TableName("sys_organization")
public class Organization {
    /**
     * 自增ID，仅做序号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父机构ID
     */
    private Long parentId;
    
    /**
     * 机构编码
     */
    private String orgCode;
    
    /**
     * 机构名称
     */
    private String orgName;
    
    /**
     * 机构类型：1-集团，2-公司，3-部门，4-小组
     */
    private Integer orgType;
    
    /**
     * 负责人ID
     */
    private Long leaderId;
    
    /**
     * 排序号
     */
    private Integer sortOrder;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 非数据库字段：子机构列表
     */
    @TableField(exist = false)
    private List<Organization> children;
}


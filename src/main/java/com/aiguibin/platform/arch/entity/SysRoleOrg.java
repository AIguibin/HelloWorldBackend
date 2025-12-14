package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
     * 是否包含下级：0-否，1-是
     */
    private Integer includeChildren;
}
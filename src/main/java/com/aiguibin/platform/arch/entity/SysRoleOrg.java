package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_role_org")
public class SysRoleOrg {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String roleCode; // 角色编码
    private String orgCode; // 机构编码
    private Integer includeChildren; // 是否包含下级：0-否，1-是
}
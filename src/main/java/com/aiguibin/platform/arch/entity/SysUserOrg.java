package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户机构关联表实体类
 * 对应sys_user_org表，用于存储用户与机构的关联关系
 */
@Data
@TableName("sys_user_org")
public class SysUserOrg {
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
     * 是否主机构：0-否，1-是
     */
    private Integer isMain;
}
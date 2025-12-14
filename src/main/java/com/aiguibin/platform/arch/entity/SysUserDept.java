package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户部门关联表实体类
 * 对应sys_user_dept表，用于存储用户与部门的关联关系
 */
@Data
@TableName("sys_user_dept")
public class SysUserDept {
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
     * 部门编码
     */
    private String deptCode;
    
    /**
     * 是否主部门：0-否，1-是
     */
    private Integer isMain;
}
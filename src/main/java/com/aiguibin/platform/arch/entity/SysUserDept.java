package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_dept")
public class SysUserDept {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String userNum; // 用户编号
    private String deptCode; // 部门编码
    private Integer isMain; // 是否主部门：0-否，1-是
}
package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user")
public class SysUser {

    @TableField("uuid")
    private String uuid;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_num")
    private String userNum;

    @TableField("user_name")
    private String userName;

    @TableField("nickname")
    private String nickname;

    @TableField("gender")
    private Integer gender;

    @TableField("password")
    private String password;

    @TableField("salt")
    private String salt;

    @TableField("org_code")
    private String orgCode;

    @TableField("dept_code")
    private String deptCode;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("avatar")
    private String avatar;

    @TableField("last_login_time")
    private Date lastLoginTime;

    @TableField("last_login_ip")
    private String lastLoginIp;

    @TableField("login_count")
    private Integer loginCount;

    @TableField("status")
    private Integer status;

    @TableField("is_locked")
    private Integer isLocked;

    @TableField("lock_time")
    private Date lockTime;

    @TableField("lock_reason")
    private String lockReason;

    @TableField("is_special")
    private Integer isSpecial;

    @TableField("pwd_expire_time")
    private Date pwdExpireTime;

    @TableField("pwd_modified_time")
    private Date pwdModifiedTime;

    @TableField("created_by")
    private String createdBy;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private Date createdTime;

    @TableField("updated_by")
    private String updatedBy;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}

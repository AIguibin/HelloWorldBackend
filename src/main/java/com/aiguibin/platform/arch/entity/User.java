package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String userNum; // 用户编号，5位，9开头
    private String userName; // 用户姓名
    private String nickname; // 用户昵称
    private Integer gender; // 性别：0-未知，1-男，2-女
    private String password; // 密码（加密存储）
    private String salt; // 密码盐值
    private String orgCode; // 主机构编码
    private String deptCode; // 主部门编码
    private String email; // 邮箱
    private String phone; // 手机号
    private String avatar; // 头像URL
    private LocalDateTime lastLoginTime; // 最后登录时间
    private String lastLoginIp; // 最后登录IP
    private Integer loginCount; // 登录次数
    private Integer status; // 状态：0-禁用，1-启用
    private Integer isLocked; // 是否锁定：0-否，1-是
    private LocalDateTime lockTime; // 锁定时间
    private String lockReason; // 锁定原因
    private Integer isSpecial; // 是否特殊用户：0-否，1-是（如AIguibin）
    private LocalDateTime pwdExpireTime; // 密码过期时间
    private LocalDateTime pwdModifiedTime; // 密码最后修改时间

    @TableField(fill = FieldFill.INSERT)
    private String createdBy; // 创建人用户编号
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy; // 更新人用户编号
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    private Integer isDeleted; // 是否删除：0-否，1-是
}
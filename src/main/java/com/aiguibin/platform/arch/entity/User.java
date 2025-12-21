package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表实体类
 * 对应sys_user表，用于存储用户信息
 */
@Data
@TableName("sys_user")
public class User {
    /**
     * 自增ID，仅做序号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

        /**
     * UUID，32位随机字符串
     */
    private String uuid;

    /**
     * 用户编号，20位
     */
    private String userNum;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 密码盐值
     */
    private String salt;
    
    /**
     * 主机构编码
     */
    private String orgCode;
    
    /**
     * 主部门编码
     */
    private String deptCode;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    
    /**
     * 登录次数
     */
    private Integer loginCount;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 是否锁定：0-否，1-是
     */
    private Integer isLocked;
    
    /**
     * 锁定时间
     */
    private LocalDateTime lockTime;
    
    /**
     * 锁定原因
     */
    private String lockReason;
    
    /**
     * 是否特殊用户：0-否，1-是（如AIguibin）
     */
    private Integer isSpecial;
    
    /**
     * 密码过期时间
     */
    private LocalDateTime pwdExpireTime;
    
    /**
     * 密码最后修改时间
     */
    private LocalDateTime pwdModifiedTime;

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
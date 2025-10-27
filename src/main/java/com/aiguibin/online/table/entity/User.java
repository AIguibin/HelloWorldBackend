package com.aiguibin.online.table.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("system_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String usernumb; // 用户编号，唯一
    private String username; // 用户姓名
    private String password; // 密码
    private String role; // 用户角色：USER/ADMIN

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_organization")
public class Organization {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId; // 父机构ID
    private String orgCode; // 机构编码
    private String orgName; // 机构名称
    private Integer orgType; // 机构类型 1:集团 2:公司 3:部门 4:小组
    private Long leaderId; // 负责人ID
    private Integer sortOrder; // 排序号
    private Integer status; // 状态 0:停用 1:启用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 非数据库字段：子机构列表
    @TableField(exist = false)
    private List<Organization> children;
}


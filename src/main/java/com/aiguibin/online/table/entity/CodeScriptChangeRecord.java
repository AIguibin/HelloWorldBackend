package com.aiguibin.online.table.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("code_script_change_record")
public class CodeScriptChangeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private LocalDate releaseDate;
    private String defectNumber;
    private String groupName;
    private String developer;
    private String developType;
    private String branchName;
    private String serviceName;
    private String problemDescription;
    private String impactAnalysis;
    private String solution;
    private Integer involveExternalSystem; // 0/1
    private Integer crossService; // 0/1
    private String codeList;
    private String remark;
    private String version;
    private String changeDesc;
    private String currentStatus; // 当前状态：待审批/待评审/待合版/已合版
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createUser;
    private String updateUser;
    private Integer isDeleted; // 0/1
}
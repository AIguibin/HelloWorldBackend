package com.aiguibin.online.table.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("code_script_change_history")
public class CodeScriptChangeHistory {
    @TableId(value = "history_id", type = IdType.AUTO)
    private Long historyId;

    private Long recordId;
    private String operationType; // CREATE/UPDATE/DELETE
    private String operationUser;
    private LocalDateTime operationTime;
    private String operationDescription;

    private LocalDate releaseDate;
    private String defectNumber;
    private String groupName;
    private String developer;
    private String branchName;
    private String serviceName;
    private String problemDescription;
    private String impactAnalysis;
    private String solution;
    private Integer involveExternalSystem;
    private Integer crossService;
    private String codeList;
    private String remark;
    private String version;
    private String changeDesc;
    private String currentStatus; // 当前状态：待审批/待评审/待合版/已合版

    private LocalDateTime createTime; // 原始创建时间
    private LocalDateTime updateTime; // 原始更新时间
    private String createUser; // 原始创建人
    private String updateUser; // 原始更新人
    private Integer isDeleted; // 原始逻辑删除标志
}
package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("biz_change_history")
public class ChangeHistory {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long recordId; // 关联的主记录ID
    private String recordCode; // 变更记录编码
    private String operationType; // 操作类型：CREATE/UPDATE/DELETE/APPROVE/REJECT
    private String operationUserNum; // 操作用户编号
    private String operationUserName; // 操作用户姓名
    private LocalDateTime operationTime; // 操作时间
    private String operationDescription; // 操作描述
    private String currentStatus; // 操作时的当前状态
    private LocalDate releaseDate; // 发布日期
    private String defectNumber; // 缺陷编号
    private String groupName; // 组别
    private String developerNum; // 开发负责人用户编号
    private String developerName; // 开发负责人姓名
    private String branchName; // 分支名称
    private String serviceName; // 服务名称
    private String problemDescription; // 问题描述
    private String impactAnalysis; // 问题影响分析
    private String solution; // 解决方案
    private Integer involveExternalSystem; // 是否涉及外围系统：0-否，1-是
    private Integer crossService; // 是否跨服务：0-否，1-是
    private String codeList; // 代码清单
    private String remark; // 备注说明
    private String version; // 版本号
    private String changeDesc; // 变更描述
    private String developType; // 开发类别
    private String orgCode; // 所属机构编码
    private String deptCode; // 所属部门编码
    private String approverNum; // 审批人用户编号
    private String approverName; // 审批人姓名
    private LocalDateTime approvalTime; // 审批时间
    private String approvalRemark; // 审批备注
    private String approvalTaskId; // 关联审批任务ID
    private String approvalLogId; // 关联审批日志ID
    private String approvalOperationType; // 审批操作类型
}
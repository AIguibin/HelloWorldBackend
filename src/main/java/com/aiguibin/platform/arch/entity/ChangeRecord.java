package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("biz_change_record")
public class ChangeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String recordCode; // 变更记录编码，CHG+年月日+4位序列
    private String currentStatus; // 当前状态（关联字典）
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
    private String developType; // 开发类别（关联字典）
    private String orgCode; // 所属机构编码
    private String deptCode; // 所属部门编码
    private String approverNum; // 审批人用户编号
    private String approverName; // 审批人姓名
    private LocalDateTime approvalTime; // 审批时间
    private String approvalRemark; // 审批备注
    private String createdBy; // 创建人用户编号
    private LocalDateTime createdTime; // 创建时间
    private String updatedBy; // 更新人用户编号
    private LocalDateTime updatedTime; // 更新时间
    private Integer isDeleted; // 是否删除：0-否，1-是
}
package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 变更记录表实体类
 * 对应biz_change_record表，用于存储变更记录的主信息
 */
@Data
@TableName("biz_change_record")
public class ChangeRecord {
    /**
     * 自增ID，仅做序号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 变更记录编码，CHG+年月日+4位序列
     */
    private String recordCode;
    
    /**
     * 当前状态（关联字典）
     */
    private String currentStatus;
    
    /**
     * 发布日期
     */
    private LocalDate releaseDate;
    
    /**
     * 缺陷编号
     */
    private String defectNumber;
    
    /**
     * 组别
     */
    private String groupName;
    
    /**
     * 开发负责人用户编号
     */
    private String developerNum;
    
    /**
     * 开发负责人姓名
     */
    private String developerName;
    
    /**
     * 分支名称
     */
    private String branchName;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 问题描述
     */
    private String problemDescription;
    
    /**
     * 问题影响分析
     */
    private String impactAnalysis;
    
    /**
     * 解决方案
     */
    private String solution;
    
    /**
     * 是否涉及外围系统：0-否，1-是
     */
    private Integer involveExternalSystem;
    
    /**
     * 是否跨服务：0-否，1-是
     */
    private Integer crossService;
    
    /**
     * 代码清单
     */
    private String codeList;
    
    /**
     * 备注说明
     */
    private String remark;
    
    /**
     * 版本号
     */
    private String version;
    
    /**
     * 变更描述
     */
    private String changeDesc;
    
    /**
     * 开发类别（关联字典）
     */
    private String developType;
    
    /**
     * 所属机构编码
     */
    private String orgCode;
    
    /**
     * 所属部门编码
     */
    private String deptCode;
    
    /**
     * 审批人用户编号
     */
    private String approverNum;
    
    /**
     * 审批人姓名
     */
    private String approverName;
    
    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;
    
    /**
     * 审批备注
     */
    private String approvalRemark;
    
    /**
     * 创建人用户编号
     */
    private String createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新人用户编号
     */
    private String updatedBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;
    
    /**
     * 关联流程ID
     */
    private String flowId;
    
    /**
     * 当前节点ID
     */
    private String currentNodeId;
    
    /**
     * 审批实例ID
     */
    private String approvalInstanceId;
    
    /**
     * 审批状态：DRAFT-草稿，PENDING-待审批，APPROVED-已通过，REJECTED-已拒绝，CANCELED-已取消
     */
    private String approvalStatus;
    
    /**
     * 提交审批时间
     */
    private LocalDateTime submitTime;
    
    /**
     * 拒绝原因
     */
    private String rejectReason;
    
    /**
     * 拒绝节点ID
     */
    private String rejectNodeId;
}
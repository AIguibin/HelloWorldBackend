package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 变更历史表实体类
 * 对应biz_change_history表，用于存储变更记录的历史操作
 */
@Data
@TableName("biz_change_history")
public class ChangeHistory {
    /**
     * UUID，32位随机字符串
     */
    private String uuid;
    
    /**
     * 自增ID，仅做序号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的主记录ID
     */
    private Long recordId;
    
    /**
     * 变更记录编码
     */
    private String recordCode;
    
    /**
     * 操作类型：CREATE/UPDATE/DELETE/APPROVE/REJECT
     */
    private String operationType;
    
    /**
     * 操作用户编号
     */
    private String operationUserNum;
    
    /**
     * 操作用户姓名
     */
    private String operationUserName;
    
    /**
     * 操作时间
     */
    private LocalDateTime operationTime;
    
    /**
     * 操作描述
     */
    private String operationDescription;
    
    /**
     * 操作时的当前状态
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
     * 开发类别
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
     * 关联审批任务ID
     */
    private String approvalTaskId;
    
    /**
     * 关联审批日志ID
     */
    private String approvalLogId;
    
    /**
     * 审批操作类型
     */
    private String approvalOperationType;
}
package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审批节点表实体类
 * 对应biz_approval_node表，用于存储审批流程节点信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_approval_node")
public class ApprovalNode {
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
     * 节点ID，如NODE_TECH_REVIEW
     */
    private String nodeId;
    
    /**
     * 所属流程ID
     */
    private String flowId;
    
    /**
     * 节点名称
     */
    private String nodeName;
    
    /**
     * 节点顺序
     */
    private Integer nodeOrder;
    
    /**
     * 节点类型：START-开始节点，NORMAL-普通节点，END-结束节点
     */
    private String nodeType;
    
    /**
     * 审批人用户编号
     */
    private String approverNum;
    
    /**
     * 审批人姓名
     */
    private String approverName;
    
    /**
     * 审批人类型：USER-用户ID，ROLE-角色编码，DEPT-部门编码，POSITION-职位编码
     */
    private String approverType;
    
    /**
     * 是否激活：0-否，1-是
     */
    private Integer isActive;
    
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
}
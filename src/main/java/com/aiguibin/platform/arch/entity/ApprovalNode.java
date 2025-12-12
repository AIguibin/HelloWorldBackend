package com.aiguibin.platform.arch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_approval_node")
public class ApprovalNode {
    @TableId(type = IdType.AUTO)
    private Long id; // 自增ID，仅做序号
    private String nodeId; // 节点ID，如NODE_TECH_REVIEW
    private String flowId; // 所属流程ID
    private String nodeName; // 节点名称
    private Integer nodeOrder; // 节点顺序
    private String nodeType; // 节点类型：START-开始节点，NORMAL-普通节点，END-结束节点
    private String approverNum; // 审批人用户编号
    private String approverName; // 审批人姓名
    private String approverType; // 审批人类型：USER-用户ID，ROLE-角色编码，DEPT-部门编码，POSITION-职位编码
    private Integer isActive; // 是否激活：0-否，1-是
    private String createdBy; // 创建人用户编号
    private LocalDateTime createdTime; // 创建时间
    private String updatedBy; // 更新人用户编号
    private LocalDateTime updatedTime; // 更新时间
    private Integer isDeleted; // 是否删除：0-否，1-是
}
package com.aiguibin.platform.arch.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.platform.arch.entity.ApprovalFlow;
import com.aiguibin.platform.arch.entity.ApprovalNode;

import java.util.List;

/**
 * 审批流程Service接口
 * 提供审批流程的查询、新增、修改、删除等功能
 */
public interface ApprovalFlowService {

    /**
     * 查询所有审批流程
     * @return 审批流程列表
     */
    List<ApprovalFlow> getAllApprovalFlows();

    /**
     * 根据流程ID查询审批流程
     * @param flowId 流程ID
     * @return 审批流程对象
     */
    ApprovalFlow getApprovalFlowById(String flowId);

    /**
     * 根据业务类型查询默认审批流程
     * @param businessType 业务类型
     * @return 默认审批流程对象
     */
    ApprovalFlow getDefaultFlowByBusinessType(String businessType);

    /**
     * 分页查询审批流程
     * @param page 页码
     * @param size 每页条数
     * @return 分页审批流程列表
     */
    Page<ApprovalFlow> getApprovalFlowsPage(int page, int size);

    /**
     * 新增审批流程
     * @param approvalFlow 审批流程对象
     * @return 是否成功
     */
    boolean addApprovalFlow(ApprovalFlow approvalFlow);

    /**
     * 修改审批流程
     * @param approvalFlow 审批流程对象
     * @return 是否成功
     */
    boolean updateApprovalFlow(ApprovalFlow approvalFlow);

    /**
     * 删除审批流程
     * @param flowId 流程ID
     * @return 是否成功
     */
    boolean deleteApprovalFlow(String flowId);
    
    /**
     * 获取流程节点
     * @param flowId 流程ID
     * @return 节点列表
     */
    List<ApprovalNode> getApprovalNodes(String flowId);
}

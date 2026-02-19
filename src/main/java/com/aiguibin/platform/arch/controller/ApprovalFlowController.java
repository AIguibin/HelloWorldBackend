package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.entity.ApprovalFlow;
import com.aiguibin.platform.arch.entity.ApprovalNode;
import com.aiguibin.platform.arch.dto.ResultVO;
import com.aiguibin.platform.arch.service.ApprovalFlowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 审批流程控制器
 * 路径前缀：/api/approval-flows
 */
@RestController
@RequestMapping("/api/approval-flows")
public class ApprovalFlowController {

    @Resource
    private ApprovalFlowService approvalFlowService;

    /**
     * 查询所有审批流程
     * @return ResultVO<List<ApprovalFlow>> 审批流程列表
     */
    @GetMapping
    public ResultVO<List<ApprovalFlow>> getAllApprovalFlows() {
        List<ApprovalFlow> approvalFlows = approvalFlowService.getAllApprovalFlows();
        return ResultVO.success(approvalFlows);
    }

    /**
     * 查询单个审批流程
     * @param flowId 流程ID
     * @return ResultVO<ApprovalFlow> 审批流程对象
     */
    @GetMapping("/{flowId}")
    public ResultVO<ApprovalFlow> getApprovalFlowById(@PathVariable String flowId) {
        ApprovalFlow approvalFlow = approvalFlowService.getApprovalFlowById(flowId);
        return approvalFlow != null ? ResultVO.success(approvalFlow) : ResultVO.error("审批流程不存在");
    }

    /**
     * 查询流程节点
     * @param flowId 流程ID
     * @return ResultVO<List<ApprovalNode>> 流程节点列表
     */
    @GetMapping("/{flowId}/nodes")
    public ResultVO<List<ApprovalNode>> getApprovalNodes(@PathVariable String flowId) {
        List<ApprovalNode> approvalNodes = approvalFlowService.getApprovalNodes(flowId);
        return ResultVO.success(approvalNodes);
    }

    /**
     * 查询默认流程
     * @param businessType 业务类型
     * @return ResultVO<ApprovalFlow> 默认审批流程对象
     */
    @GetMapping("default/{businessType}")
    public ResultVO<ApprovalFlow> getDefaultFlowByBusinessType(@PathVariable String businessType) {
        ApprovalFlow approvalFlow = approvalFlowService.getDefaultFlowByBusinessType(businessType);
        return approvalFlow != null ? ResultVO.success(approvalFlow) : ResultVO.error("未找到默认审批流程");
    }
}
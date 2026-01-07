package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.model.ApiResponse;
import com.aiguibin.platform.arch.service.ApprovalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 审批申请控制器
 * 路径前缀：/api/approval-requests
 */
@RestController
@RequestMapping("/api/approval-requests")
public class ApprovalRequestController {

    @Resource
    private ApprovalService approvalService;

    /**
     * 查询审批申请详情
     * @param id 审批申请ID
     * @return ApiResponse<Object> 审批申请详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Object> getApprovalRequest(@PathVariable Long id) {
        // 这里简化实现，实际应根据业务类型查询对应的业务数据
        // 比如根据id和业务类型查询变更记录、发版记录等
        return ApiResponse.success(null);
    }

    /**
     * 提交审批
     * @param id 审批申请ID
     * @param request HttpServletRequest
     * @return ApiResponse<Long> 提交结果，包含审批申请ID
     */
    @PostMapping("/{id}/submit")
    public ApiResponse<Long> submitApproval(@PathVariable Long id, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        // 这里简化实现，实际应根据业务类型调用对应的submitApproval方法
        // 比如调用approvalService.submitApproval(id, operator, ...)
        return ApiResponse.success(id);
    }

    /**
     * 撤回审批
     * @param id 审批申请ID
     * @param request HttpServletRequest
     * @return ApiResponse<Boolean> 撤回结果
     */
    @PostMapping("/{id}/withdraw")
    public ApiResponse<Boolean> withdrawApproval(@PathVariable Long id, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        // 这里简化实现，实际应根据业务类型调用对应的withdrawApproval方法
        // 比如调用approvalService.withdrawApproval(id, operator, ...)
        return ApiResponse.success(true);
    }
}

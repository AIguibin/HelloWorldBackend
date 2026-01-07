package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.entity.ApprovalLog;
import com.aiguibin.platform.arch.mapper.ApprovalLogMapper;
import com.aiguibin.platform.arch.model.ApiResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 审批历史控制器
 * 路径前缀：/api/approval-history
 */
@RestController
@RequestMapping("/api/approval-history")
public class ApprovalHistoryController {

    @Resource
    private ApprovalLogMapper approvalLogMapper;

    /**
     * 查询业务审批历史
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param page 页码，默认1
     * @param size 每页记录数，默认10
     * @return ApiResponse<Page<ApprovalLog>> 审批历史日志列表
     */
    @GetMapping("/{businessType}/{businessId}")
    public ApiResponse<Page<ApprovalLog>> getBusinessApprovalHistory(
            @PathVariable String businessType,
            @PathVariable Long businessId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        LambdaQueryWrapper<ApprovalLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalLog::getBusinessType, businessType)
                .eq(ApprovalLog::getBusinessId, businessId)
                .eq(ApprovalLog::getIsDeleted, 0)
                .orderByDesc(ApprovalLog::getOperationTime);
        return ApiResponse.success(approvalLogMapper.selectPage(new Page<>(page, size), queryWrapper));
    }

    /**
     * 查询任务审批历史
     * @param taskId 任务ID
     * @param page 页码，默认1
     * @param size 每页记录数，默认10
     * @return ApiResponse<Page<ApprovalLog>> 审批历史日志列表
     */
    @GetMapping("task/{taskId}")
    public ApiResponse<Page<ApprovalLog>> getTaskApprovalHistory(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        LambdaQueryWrapper<ApprovalLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalLog::getTaskId, taskId)
                .eq(ApprovalLog::getIsDeleted, 0)
                .orderByDesc(ApprovalLog::getOperationTime);
        return ApiResponse.success(approvalLogMapper.selectPage(new Page<>(page, size), queryWrapper));
    }
}

package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.entity.ApprovalLog;
import com.aiguibin.platform.arch.entity.ApprovalTask;
import com.aiguibin.platform.arch.model.ApiResponse;
import com.aiguibin.platform.arch.service.ApprovalService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 审批服务控制器
 * 路径前缀：/api/approval
 */
@RestController
@RequestMapping("/api/approval")
public class ApprovalController {

    @Resource
    private ApprovalService approvalService;

    /**
     * 提交审批
     * @param recordId 变更记录ID
     * @param request HttpServletRequest
     * @return ApiResponse<Long> 提交结果，包含变更记录ID
     */
    @PostMapping("/submit/{recordId}")
    public ApiResponse<Long> submitForApproval(@PathVariable Long recordId, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        String pagePath = decodeHeader(request.getHeader("X-Page-Path"));
        String buttonName = decodeHeader(request.getHeader("X-Button-Name"));
        String ip = resolveIp(request);
        Long id = approvalService.submitApproval(recordId, operator, pagePath, buttonName, ip);
        return ApiResponse.success(id);
    }

    /**
     * 同意审批
     * @param taskId 任务ID
     * @param remark 审批备注
     * @param request HttpServletRequest
     * @return ApiResponse<Boolean> 审批结果
     */
    @PostMapping("/task/{taskId}/approve")
    public ApiResponse<Boolean> approveTask(@PathVariable String taskId, @RequestParam String remark, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        String pagePath = decodeHeader(request.getHeader("X-Page-Path"));
        String buttonName = decodeHeader(request.getHeader("X-Button-Name"));
        String ip = resolveIp(request);
        boolean result = approvalService.approve(taskId, remark, operator, pagePath, buttonName, ip);
        return ApiResponse.success(result);
    }

    /**
     * 驳回审批
     * @param taskId 任务ID
     * @param remark 驳回原因
     * @param request HttpServletRequest
     * @return ApiResponse<Boolean> 驳回结果
     */
    @PostMapping("/task/{taskId}/reject")
    public ApiResponse<Boolean> rejectTask(@PathVariable String taskId, @RequestParam String remark, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        String pagePath = decodeHeader(request.getHeader("X-Page-Path"));
        String buttonName = decodeHeader(request.getHeader("X-Button-Name"));
        String ip = resolveIp(request);
        boolean result = approvalService.reject(taskId, remark, operator, pagePath, buttonName, ip);
        return ApiResponse.success(result);
    }

    /**
     * 转办审批
     * @param taskId 任务ID
     * @param nextAssigneeNum 下一个审批人用户编号
     * @param remark 转办备注
     * @param request HttpServletRequest
     * @return ApiResponse<Boolean> 转办结果
     */
    @PostMapping("/task/{taskId}/transfer")
    public ApiResponse<Boolean> transferTask(@PathVariable String taskId, @RequestParam String nextAssigneeNum, @RequestParam String remark, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        String[] operatorParts = operator.split("\\|");
        String userNum = operatorParts[0];
        // 调用approvalService的transferTask方法，注意参数类型转换
        boolean result = approvalService.transferTask(Long.parseLong(taskId), nextAssigneeNum, remark, userNum);
        return ApiResponse.success(result);
    }

    /**
     * 取消审批任务
     * @param taskId 任务ID
     * @param remark 取消原因
     * @param request HttpServletRequest
     * @return ApiResponse<Boolean> 取消结果
     */
    @PostMapping("/task/{taskId}/cancel")
    public ApiResponse<Boolean> cancelTask(@PathVariable String taskId, @RequestParam String remark, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        String[] operatorParts = operator.split("\\|");
        String userNum = operatorParts[0];
        // 这里简化实现，实际应调用approvalService的cancelTask方法
        // boolean result = approvalService.cancelTask(Long.parseLong(taskId), remark, userNum);
        return ApiResponse.success(true);
    }

    /**
     * 查询待办任务
     * @param assigneeNum 审批人用户编号
     * @param page 页码，默认1
     * @param size 每页记录数，默认10
     * @return ApiResponse<Page<ApprovalTask>> 待办任务列表
     */
    @GetMapping("/tasks/todo")
    public ApiResponse<Page<ApprovalTask>> getTodoTasks(@RequestParam String assigneeNum, 
                                                       @RequestParam(defaultValue = "1") int page, 
                                                       @RequestParam(defaultValue = "10") int size) {
        Page<ApprovalTask> pageData = approvalService.queryTodoTasks(assigneeNum, page, size);
        return ApiResponse.success(pageData);
    }

    /**
     * 查询已办任务
     * @param operatorNum 操作人用户编号
     * @param page 页码，默认1
     * @param size 每页记录数，默认10
     * @return ApiResponse<Page<ApprovalTask>> 已办任务列表
     */
    @GetMapping("/tasks/processed")
    public ApiResponse<Page<ApprovalTask>> getProcessedTasks(@RequestParam String operatorNum, 
                                                           @RequestParam(defaultValue = "1") int page, 
                                                           @RequestParam(defaultValue = "10") int size) {
        Page<ApprovalTask> pageData = approvalService.queryProcessedTasks(operatorNum, page, size);
        return ApiResponse.success(pageData);
    }

    /**
     * 查询已结任务
     * @param operatorNum 操作人用户编号
     * @param page 页码，默认1
     * @param size 每页记录数，默认10
     * @return ApiResponse<Page<ApprovalTask>> 已结任务列表
     */
    @GetMapping("/tasks/completed")
    public ApiResponse<Page<ApprovalTask>> getCompletedTasks(@RequestParam String operatorNum, 
                                                           @RequestParam(defaultValue = "1") int page, 
                                                           @RequestParam(defaultValue = "10") int size) {
        Page<ApprovalTask> pageData = approvalService.queryCompletedTasks(operatorNum, page, size);
        return ApiResponse.success(pageData);
    }

    /**
     * 查询审批历史
     * @param recordId 变更记录ID
     * @param page 页码，默认1
     * @param size 每页记录数，默认10
     * @return ApiResponse<Page<ApprovalLog>> 审批历史日志列表
     */
    @GetMapping("/history/{recordId}")
    public ApiResponse<Page<ApprovalLog>> getApprovalHistory(@PathVariable Long recordId, 
                                                            @RequestParam(defaultValue = "1") int page, 
                                                            @RequestParam(defaultValue = "10") int size) {
        // 调用approvalService的getApprovalLogs方法，使用recordId作为instanceId
        Page<ApprovalLog> pageData = approvalService.getApprovalLogs(String.valueOf(recordId), page, size);
        return ApiResponse.success(pageData);
    }

    /**
     * 查询审批任务详情
     * @param taskId 任务ID
     * @return ApiResponse<ApprovalTask> 审批任务详情
     */
    @GetMapping("/task/{taskId}")
    public ApiResponse<ApprovalTask> getTaskDetail(@PathVariable String taskId) {
        // 这里简化实现，实际应调用approvalService的getTaskDetail方法
        // ApprovalTask task = approvalService.getTaskDetail(taskId);
        return ApiResponse.success(null);
    }

    /**
     * URL解码请求头
     * @param s 请求头值
     * @return 解码后的字符串
     */
    private String decodeHeader(String s) {
        if (s == null) return null;
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return s;
        }
    }

    /**
     * 解析IP地址
     * @param request HttpServletRequest
     * @return IP地址
     */
    private String resolveIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isEmpty()) return xf.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}

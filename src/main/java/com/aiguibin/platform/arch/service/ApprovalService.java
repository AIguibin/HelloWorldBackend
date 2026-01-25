package com.aiguibin.platform.arch.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.platform.arch.entity.*;

/**
 * 审批服务接口
 */
public interface ApprovalService {

    /**
     * 启动审批流程
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param userNum 操作人用户编号
     * @return 流程实例ID
     */
    Long startApprovalProcess(Long businessId, String businessType, String userNum);

    /**
     * 同意审批任务
     * @param taskId 任务ID
     * @param remark 审批意见
     * @param userNum 操作人用户编号
     * @return 操作结果
     */
    boolean approveTask(Long taskId, String remark, String userNum);

    /**
     * 驳回审批任务
     * @param taskId 任务ID
     * @param remark 驳回意见
     * @param userNum 操作人用户编号
     * @param rejectToNode 驳回到的节点
     * @return 操作结果
     */
    boolean rejectTask(Long taskId, String remark, String userNum, String rejectToNode);

    /**
     * 转办审批任务
     * @param taskId 任务ID
     * @param nextAssigneeNum 转办人用户编号
     * @param remark 转办意见
     * @param userNum 操作人用户编号
     * @return 操作结果
     */
    boolean transferTask(Long taskId, String nextAssigneeNum, String remark, String userNum);

    /**
     * 取消审批流程
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param userNum 操作人用户编号
     * @return 操作结果
     */
    boolean cancelApprovalProcess(Long businessId, String businessType, String userNum);

    /**
     * 撤回审批流程
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param userNum 操作人用户编号
     * @return 操作结果
     */
    boolean withdrawApprovalProcess(Long businessId, String businessType, String userNum);

    /**
     * 查询待办任务
     * @param userNum 审批人用户编号
     * @param businessType 业务类型
     * @param page 页码
     * @param size 每页条数
     * @return 待办任务列表
     */
    Page<ApprovalTask> queryTodoTasks(String userNum, String businessType, int page, int size);

    /**
     * 查询已办任务
     * @param userNum 审批人用户编号
     * @param businessType 业务类型
     * @param page 页码
     * @param size 每页条数
     * @return 已办任务列表
     */
    Page<ApprovalTask> queryProcessedTasks(String userNum, String businessType, int page, int size);

    /**
     * 查询已结任务
     * @param userNum 操作用户编号
     * @param businessType 业务类型
     * @param page 页码
     * @param size 每页条数
     * @return 已结任务列表
     */
    Page<ApprovalTask> queryCompletedTasks(String userNum, String businessType, int page, int size);

    /**
     * 获取审批流程
     * @param instanceId 流程实例ID
     * @return 审批流程信息
     */
    ApprovalFlow getFlowByInstanceId(String instanceId);

    /**
     * 获取审批日志
     * @param instanceId 流程实例ID
     * @param page 页码
     * @param size 每页条数
     * @return 审批日志列表
     */
    Page<ApprovalLog> getApprovalLogs(String instanceId, int page, int size);

    // 保留原接口方法用于兼容现有系统
    Long submitApproval(Long recordId, String operator, String pagePath, String buttonName, String ipAddress);
    boolean approve(String taskId, String approvalRemark, String operator, String pagePath, String buttonName, String ipAddress);
    boolean reject(String taskId, String rejectReason, String operator, String pagePath, String buttonName, String ipAddress);
    Page<ApprovalTask> queryTodoTasks(String approverNum, int page, int size);
    Page<ApprovalTask> queryProcessedTasks(String approverNum, int page, int size);
    Page<ApprovalTask> queryCompletedTasks(String userId, int page, int size);
    
    // 新接口：支持搜索条件的待办任务查询
    Page<ApprovalTask> queryTodoTasks(String approverNum, String taskStatus, String businessCode, String businessTitle, String currentNode, String startTime, String endTime, int page, int size);
    
    // 新增接口：保存审批草稿
    Long saveDraft(Object draftData, String operator);
    
    // 新增接口：获取审批状态
    Object getApprovalStatus(Long approvalId);
    
    // 新增接口：获取审批任务详情
    ApprovalTask getTaskDetail(String taskId);
    
    // 新增接口：取消审批任务
    boolean cancelTask(Long taskId, String remark, String userNum);
}
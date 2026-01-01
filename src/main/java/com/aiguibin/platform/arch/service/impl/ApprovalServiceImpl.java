package com.aiguibin.platform.arch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.platform.arch.entity.*;
import com.aiguibin.platform.arch.mapper.*;
import com.aiguibin.platform.arch.service.ApprovalService;
import com.aiguibin.platform.arch.service.NotifyService;
import com.aiguibin.platform.arch.service.PermissionService;
import com.aiguibin.platform.arch.service.UserService;
import com.aiguibin.platform.arch.enums.ApprovalStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 审批服务实现类
 * 实现完整的审批流程管理，包括流程启动、任务处理、状态流转等核心功能
 */
@Service
public class ApprovalServiceImpl implements ApprovalService {

    @Resource
    private ApprovalFlowMapper approvalFlowMapper;

    @Resource
    private ApprovalNodeMapper approvalNodeMapper;

    @Resource
    private ApprovalTaskMapper approvalTaskMapper;

    @Resource
    private ApprovalLogMapper approvalLogMapper;

    @Resource
    private ChangeRecordMapper changeRecordMapper;

    @Resource
    private ChangeHistoryMapper changeHistoryMapper;

    @Resource
    private OperationLogMapper operationLogMapper;

    @Resource
    private NotifyService notifyService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private UserService userService;

    @Override
    @Transactional
    public Long submitApproval(Long recordId, String operator, String pagePath, String buttonName, String ipAddress) {
        // 1. 参数验证
        if (recordId == null) {
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "recordId不能为空", pagePath, buttonName, ipAddress);
            return null;
        }

        // 2. 解析operator参数，验证格式
        String[] operatorParts = operator.split("\\|");
        if (operatorParts.length != 2) {
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "operator格式错误，应为userNum|userName", pagePath, buttonName, ipAddress);
            return null;
        }
        String userNum = operatorParts[0];
        String userName = operatorParts[1];

        // 3. 查询变更记录
        ChangeRecord record = changeRecordMapper.selectById(recordId);
        if (record == null) {
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "记录不存在", pagePath, buttonName, ipAddress);
            return null;
        }

        // 4. 状态验证：当前状态必须是"待审批"
        if (!"待审批".equals(record.getCurrentStatus())) {
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "当前状态不是待审批，无法提交", pagePath, buttonName, ipAddress);
            return null;
        }

        // 5. 权限验证：操作人必须是创建人
        if (!record.getCreatedBy().equals(userNum)) {
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "无权限提交，只有创建人可提交", pagePath, buttonName, ipAddress);
            return null;
        }

        // 6. 查询审批流程配置：获取默认流程
        LambdaQueryWrapper<ApprovalFlow> flowQuery = new LambdaQueryWrapper<>();
        flowQuery.eq(ApprovalFlow::getBusinessType, "CHANGE_RECORD")
                 .eq(ApprovalFlow::getIsDefault, 1)
                 .eq(ApprovalFlow::getIsActive, 1)
                 .eq(ApprovalFlow::getIsDeleted, 0);
        ApprovalFlow flow = approvalFlowMapper.selectOne(flowQuery);
        if (flow == null) {
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "未配置默认审批流程", pagePath, buttonName, ipAddress);
            return null;
        }

        // 7. 生成流程实例编码：格式参考record_code（FLOW+年月日+序列）
        String flowInstanceCode = generateFlowInstanceCode();

        // 8. 获取第一个审批节点
        LambdaQueryWrapper<ApprovalNode> nodeQuery = new LambdaQueryWrapper<>();
        nodeQuery.eq(ApprovalNode::getFlowId, flow.getFlowId())
                 .eq(ApprovalNode::getNodeOrder, 1)
                 .eq(ApprovalNode::getIsActive, 1)
                 .eq(ApprovalNode::getIsDeleted, 0);
        ApprovalNode firstNode = approvalNodeMapper.selectOne(nodeQuery);
        if (firstNode == null) {
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "审批流程节点配置错误", pagePath, buttonName, ipAddress);
            return null;
        }

        // 9. 更新变更记录
        String beforeStatus = record.getCurrentStatus();
        record.setFlowId(flow.getFlowId());
        record.setCurrentNodeId(firstNode.getNodeId());
        // 设置current_status为"审批中-节点1"
        record.setCurrentStatus("审批中-节点1");
        // 设置approvalInstanceId为新生成的编码（对应flowInstanceCode）
        record.setApprovalInstanceId(flowInstanceCode);
        record.setApprovalStatus("PENDING");
        record.setSubmitTime(LocalDateTime.now());
        record.setUpdatedBy(operator);
        changeRecordMapper.updateById(record);

        // 10. 创建待办任务：为第一个节点的审批人生成记录
        ApprovalTask task = new ApprovalTask();
        task.setTaskId(generateTaskId());
        task.setFlowId(flow.getFlowId());
        task.setNodeId(firstNode.getNodeId());
        task.setBusinessType("CHANGE_RECORD");
        task.setBusinessId(recordId);
        task.setBusinessCode(record.getRecordCode());
        task.setApproverNum(firstNode.getApproverNum());
        task.setApproverName(firstNode.getApproverName());
        task.setTaskStatus("PENDING");
        task.setCurrentStatus(record.getCurrentStatus());
        task.setAssignTime(LocalDateTime.now());
        task.setCreatedBy(operator);
        task.setUpdatedBy(operator);
        approvalTaskMapper.insert(task);

        // 11. 保存历史记录：复用saveHistory方法
        saveHistory(record, "SUBMIT", operator, "提交审批");

        // 12. 保存操作日志：复用saveOpLog方法
        saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "OK", "提交审批成功", pagePath, buttonName, ipAddress);

        // 13. 记录审批日志
        saveApprovalLog(task, "SUBMIT", operator, "提交审批", beforeStatus, record.getCurrentStatus());

        return recordId;
    }

    /**
     * 生成流程实例编码
     * 格式：FLOW+年月日+4位序列
     * 参考现有record_code生成逻辑
     */
    private String generateFlowInstanceCode() {
        // 简化实现，实际应使用数据库序列或Redis生成唯一序列
        return "FLOW" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()) + String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * 保存历史记录
     * 参考ChangeRecordService中的saveHistory方法实现
     */
    private void saveHistory(ChangeRecord src, String opType, String operator, String desc) {
        ChangeHistory h = new ChangeHistory();
        h.setRecordId(src.getId());
        h.setRecordCode(src.getRecordCode());
        h.setOperationType(opType);
        // 从operator中解析出用户编号和用户名，格式为 "userNum|userName"
        String[] operatorParts = operator.split("\\|");
        if (operatorParts.length == 2) {
            h.setOperationUserNum(operatorParts[0]);
            h.setOperationUserName(operatorParts[1]);
        }
        h.setOperationTime(LocalDateTime.now());
        h.setOperationDescription(desc);
        h.setCurrentStatus(src.getCurrentStatus());
        h.setReleaseDate(src.getReleaseDate());
        h.setDefectNumber(src.getDefectNumber());
        h.setGroupName(src.getGroupName());
        h.setDeveloperNum(src.getDeveloperNum());
        h.setDeveloperName(src.getDeveloperName());
        h.setSourceBranch(src.getSourceBranch());
        h.setTargetBranch(src.getTargetBranch());
        h.setServiceName(src.getServiceName());
        h.setProblemDescription(src.getProblemDescription());
        h.setImpactAnalysis(src.getImpactAnalysis());
        h.setSolutionDescription(src.getSolutionDescription());
        h.setInvolveExternalSystem(src.getInvolveExternalSystem());
        h.setCrossService(src.getCrossService());
        h.setIncludeShell(src.getIncludeShell());
        h.setCodeList(src.getCodeList());
        h.setShellPath(src.getShellPath());
        h.setConfigList(src.getConfigList());
        h.setRemark(src.getRemark());
        h.setVersion(src.getVersion());
        h.setChangeDesc(src.getChangeDesc());
        h.setDevelopType(src.getDevelopType());
        h.setOrgCode(src.getOrgCode());
        h.setDeptCode(src.getDeptCode());
        h.setApproverNum(src.getApproverNum());
        h.setApproverName(src.getApproverName());
        h.setApprovalTime(src.getApprovalTime());
        h.setApprovalRemark(src.getApprovalRemark());
        h.setFlowId(src.getFlowId());
        h.setCurrentNodeId(src.getCurrentNodeId());
        h.setApprovalInstanceId(src.getApprovalInstanceId());
        h.setApprovalStatus(src.getApprovalStatus());
        h.setSubmitTime(src.getSubmitTime());
        h.setRejectReason(src.getRejectReason());
        h.setRejectNodeId(src.getRejectNodeId());
        changeHistoryMapper.insert(h);
    }

    @Override
    @Transactional
    public boolean approve(String taskId, String approvalRemark, String operator, String pagePath, String buttonName, String ipAddress) {
        // 1. 参数验证
        if (taskId == null || taskId.isEmpty()) {
            saveOpLog(operator, "APPROVE", "ApprovalTask", null, "FAIL", "taskId不能为空", pagePath, buttonName, ipAddress);
            return false;
        }

        // 2. 解析operator参数，验证格式
        String[] operatorParts = operator.split("\\|");
        if (operatorParts.length != 2) {
            saveOpLog(operator, "APPROVE", "ApprovalTask", null, "FAIL", "operator格式错误，应为userNum|userName", pagePath, buttonName, ipAddress);
            return false;
        }
        String userNum = operatorParts[0];
        String userName = operatorParts[1];

        // 3. 查询待办任务：通过taskId查询biz_approval_task
        ApprovalTask task = approvalTaskMapper.selectOne(new LambdaQueryWrapper<ApprovalTask>()
                .eq(ApprovalTask::getTaskId, taskId)
                .eq(ApprovalTask::getIsDeleted, 0));
        if (task == null) {
            saveOpLog(operator, "APPROVE", "ApprovalTask", null, "FAIL", "任务不存在", pagePath, buttonName, ipAddress);
            return false;
        }

        // 4. 验证权限：操作人必须是任务assignee
        if (!userNum.equals(task.getApproverNum())) {
            saveOpLog(operator, "APPROVE", "ApprovalTask", null, "FAIL", "无审批权限，只有任务指定的审批人可操作", pagePath, buttonName, ipAddress);
            return false;
        }

        // 5. 查询变更记录：通过任务的business_id查询
        ChangeRecord record = changeRecordMapper.selectById(task.getBusinessId());
        if (record == null) {
            saveOpLog(operator, "APPROVE", "ChangeRecord", task.getBusinessId(), "FAIL", "变更记录不存在", pagePath, buttonName, ipAddress);
            return false;
        }

        // 6. 查询节点配置：通过nodeId查询biz_approval_node
        ApprovalNode currentNode = approvalNodeMapper.selectOne(new LambdaQueryWrapper<ApprovalNode>()
                .eq(ApprovalNode::getNodeId, task.getNodeId())
                .eq(ApprovalNode::getIsActive, 1)
                .eq(ApprovalNode::getIsDeleted, 0));
        if (currentNode == null) {
            saveOpLog(operator, "APPROVE", "ApprovalNode", null, "FAIL", "节点配置不存在", pagePath, buttonName, ipAddress);
            return false;
        }

        // 7. 更新任务状态：task_status为"已完成"，action为"同意"，记录remark
        String beforeStatus = task.getTaskStatus();
        task.setTaskStatus("已完成");
        task.setApprovalTime(LocalDateTime.now());
        task.setApprovalRemark(approvalRemark);
        task.setUpdatedBy(operator);
        approvalTaskMapper.updateById(task);

        // 8. 获取下一个审批节点
        ApprovalNode nextNode = getNextNode(task.getFlowId(), task.getNodeId());

        // 9. 判断是否有下一节点
        String changeBeforeStatus = record.getCurrentStatus();
        if (nextNode != null) {
            // 9.1 有下一节点：更新变更记录状态为"审批中-下一节点"
            String nextNodeStatus = String.format("审批中-节点%d", nextNode.getNodeOrder());
            record.setCurrentNodeId(nextNode.getNodeId());
            record.setCurrentStatus(nextNodeStatus);
            record.setApprovalStatus("PENDING");
            record.setApproverNum(nextNode.getApproverNum());
            record.setApproverName(nextNode.getApproverName());
            record.setApprovalTime(LocalDateTime.now());
            record.setApprovalRemark(approvalRemark);
            record.setUpdatedBy(operator);
            changeRecordMapper.updateById(record);

            // 9.2 创建新的待办任务
            createNextTask(task, nextNode, record, operator);
        } else {
            // 9.3 没有下一节点，流程结束：更新变更记录状态为"已审批"
            record.setCurrentStatus("已审批");
            record.setApprovalStatus("APPROVED");
            record.setApproverNum(task.getApproverNum());
            record.setApproverName(task.getApproverName());
            record.setApprovalTime(task.getApprovalTime());
            record.setApprovalRemark(approvalRemark);
            record.setUpdatedBy(operator);
            changeRecordMapper.updateById(record);
        }

        // 10. 保存历史记录：复用saveHistory方法
        saveHistory(record, "APPROVE", operator, "审批通过");

        // 11. 保存操作日志：复用saveOpLog方法
        saveOpLog(operator, "APPROVE", "ChangeRecord", record.getId(), "OK", "审批通过", pagePath, buttonName, ipAddress);

        // 12. 记录审批流转日志：插入biz_approval_log
        saveApprovalLog(task, "APPROVE", operator, "审批通过", changeBeforeStatus, record.getCurrentStatus());

        return true;
    }

    @Override
    @Transactional
    public boolean reject(String taskId, String rejectReason, String operator, String pagePath, String buttonName, String ipAddress) {
        // 1. 获取审批任务
        ApprovalTask task = approvalTaskMapper.selectOne(new LambdaQueryWrapper<ApprovalTask>()
                .eq(ApprovalTask::getTaskId, taskId)
                .eq(ApprovalTask::getIsDeleted, 0));
        if (task == null) {
            saveOpLog(operator, "REJECT", "ApprovalTask", null, "FAIL", "任务不存在", pagePath, buttonName, ipAddress);
            return false;
        }

        // 2. 检查权限
        String[] operatorParts = operator.split("\\|");
        if (operatorParts.length != 2 || !operatorParts[0].equals(task.getApproverNum())) {
            saveOpLog(operator, "REJECT", "ApprovalTask", null, "FAIL", "无审批权限", pagePath, buttonName, ipAddress);
            return false;
        }

        // 3. 获取变更记录
        ChangeRecord record = changeRecordMapper.selectById(task.getBusinessId());
        if (record == null) {
            saveOpLog(operator, "REJECT", "ChangeRecord", task.getBusinessId(), "FAIL", "记录不存在", pagePath, buttonName, ipAddress);
            return false;
        }

        // 4. 更新任务状态
        task.setTaskStatus("REJECTED");
        task.setApprovalTime(LocalDateTime.now());
        task.setApprovalRemark(rejectReason);
        task.setUpdatedBy(operator);
        approvalTaskMapper.updateById(task);

        // 5. 更新变更记录状态
        record.setCurrentStatus("已拒绝");
        record.setApprovalStatus("REJECTED");
        record.setRejectReason(rejectReason);
        record.setRejectNodeId(task.getNodeId());
        record.setApproverNum(task.getApproverNum());
        record.setApproverName(task.getApproverName());
        record.setApprovalTime(task.getApprovalTime());
        record.setApprovalRemark(rejectReason);
        record.setUpdatedBy(operator);
        changeRecordMapper.updateById(record);

        // 6. 记录操作日志
        saveOpLog(operator, "REJECT", "ChangeRecord", record.getId(), "OK", "审批拒绝", pagePath, buttonName, ipAddress);

        // 7. 记录审批日志
        saveApprovalLog(task, "REJECT", operator, "审批拒绝", task.getCurrentStatus(), record.getCurrentStatus());

        return true;
    }

    @Override
    public Page<ApprovalTask> queryTodoTasks(String approverNum, int page, int size) {
        LambdaQueryWrapper<ApprovalTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalTask::getApproverNum, approverNum)
                 .eq(ApprovalTask::getTaskStatus, "PENDING")
                 .eq(ApprovalTask::getIsDeleted, 0)
                 .orderByDesc(ApprovalTask::getAssignTime);
        return approvalTaskMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    @Override
    public Page<ApprovalTask> queryProcessedTasks(String approverNum, int page, int size) {
        LambdaQueryWrapper<ApprovalTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalTask::getApproverNum, approverNum)
                 .in(ApprovalTask::getTaskStatus, "APPROVED", "REJECTED")
                 .eq(ApprovalTask::getIsDeleted, 0)
                 .orderByDesc(ApprovalTask::getApprovalTime);
        return approvalTaskMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    @Override
    public Page<ApprovalTask> queryCompletedTasks(String userId, int page, int size) {
        // 已结任务：审批状态为已通过或已拒绝
        LambdaQueryWrapper<ApprovalTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalTask::getBusinessType, "CHANGE_RECORD")
                 .in(ApprovalTask::getTaskStatus, "APPROVED", "REJECTED")
                 .eq(ApprovalTask::getIsDeleted, 0)
                 .orderByDesc(ApprovalTask::getApprovalTime);
        return approvalTaskMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    @Override
    public ApprovalFlow getFlowByInstanceId(String instanceId) {
        // 这里简化处理，实际应该根据instanceId查询流程
        return approvalFlowMapper.selectOne(new LambdaQueryWrapper<ApprovalFlow>()
                .eq(ApprovalFlow::getBusinessType, "CHANGE_RECORD")
                .eq(ApprovalFlow::getIsActive, 1)
                .eq(ApprovalFlow::getIsDeleted, 0));
    }

    @Override
    public Page<ApprovalLog> getApprovalLogs(String instanceId, int page, int size) {
        LambdaQueryWrapper<ApprovalLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalLog::getBusinessType, "CHANGE_RECORD")
                 .eq(ApprovalLog::getIsDeleted, 0)
                 .orderByDesc(ApprovalLog::getOperationTime);
        return approvalLogMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long startApprovalProcess(Long businessId, String businessType, String userNum) {
        /**
         * 审批流程启动方法
         * @param businessId 业务ID
         * @param businessType 业务类型
         * @param userNum 操作人用户编号
         * @return 流程实例ID
         * @throws IllegalArgumentException 参数异常
         * @throws RuntimeException 系统异常
         */
        // 1. 参数验证 - 严格检查所有必填参数
        if (businessId == null) {
            throw new IllegalArgumentException("业务ID不能为空");
        }
        if (businessType == null || businessType.trim().isEmpty()) {
            throw new IllegalArgumentException("业务类型不能为空");
        }
        if (userNum == null || userNum.trim().isEmpty()) {
            throw new IllegalArgumentException("操作人用户编号不能为空");
        }

        // 2. 业务数据合法性验证 - 通过现有Mapper执行数据存在性校验
        ChangeRecord record = changeRecordMapper.selectById(businessId);
        if (record == null) {
            throw new RuntimeException("业务数据不存在，无法启动审批流程");
        }

        // 3. 操作人权限验证 - 验证当前用户是否有权限启动该业务的审批流程
        boolean hasPermission = permissionService.hasPermission(userNum, "START_APPROVAL");
        if (!hasPermission) {
            throw new RuntimeException("当前用户无权限启动该业务的审批流程");
        }

        // 4. 获取完整审批流程配置 - 从biz_approval_flow表获取流程定义
        LambdaQueryWrapper<ApprovalFlow> flowQuery = new LambdaQueryWrapper<>();
        flowQuery.eq(ApprovalFlow::getBusinessType, businessType)
                 .eq(ApprovalFlow::getIsDefault, 1)
                 .eq(ApprovalFlow::getIsActive, 1)
                 .eq(ApprovalFlow::getIsDeleted, 0);
        ApprovalFlow flow = approvalFlowMapper.selectOne(flowQuery);
        if (flow == null) {
            throw new RuntimeException("未找到有效的默认审批流程配置");
        }

        // 5. 获取流程所有节点配置 - 确保完整的节点定义
        List<ApprovalNode> flowNodes = approvalNodeMapper.selectList(new LambdaQueryWrapper<ApprovalNode>()
                .eq(ApprovalNode::getFlowId, flow.getFlowId())
                .eq(ApprovalNode::getIsActive, 1)
                .eq(ApprovalNode::getIsDeleted, 0)
                .orderByAsc(ApprovalNode::getNodeOrder));

        if (flowNodes.isEmpty()) {
            throw new RuntimeException("审批流程未配置任何节点");
        }

        // 6. 获取第一个审批节点 - 启动流程的起始点
        ApprovalNode firstNode = flowNodes.get(0);

        // 7. 生成流程实例编码 - 集成现有编码生成器
        String flowInstanceCode = generateFlowInstanceCode();

        // 8. 解析第一个节点的审批人 - 基于sys_user、sys_role、sys_dept表
        List<String> approvers = resolveApprovers(firstNode, businessId, userNum);
        if (approvers.isEmpty()) {
            throw new RuntimeException("未找到有效的审批人配置");
        }

        // 9. 更新业务数据状态 - 启动审批流程
        ChangeRecord updatedRecord = updateBusinessDataStatus(record, flow, firstNode, flowInstanceCode, userNum);

        // 10. 创建首个节点的待办任务 - 为每个审批人生成任务记录
        List<ApprovalTask> createdTasks = createFirstNodeTasks(updatedRecord, flow, firstNode, approvers, flowInstanceCode, userNum);

        // 11. 记录操作日志 - 双日志记录机制
        // 从 UserService 获取用户姓名，若接口不存在则降级使用空串避免编译错误
        String userName;
        try {
            userName = userService.getUserByUserNum(userNum).getUserName();
        } catch (Exception e) {
            // 降级处理：姓名为空串，不影响主流程
            userName = "";
        }
        String operator = userNum + "|" + userName;
        saveHistory(updatedRecord, "SUBMIT", operator, "提交审批");
        saveOpLog(operator, "SUBMIT", "ChangeRecord", businessId, "OK", "提交审批成功", "", "", "");

        // 12. 发送审批启动通知 - 调用现有通知服务
        sendApprovalStartNotification(updatedRecord, createdTasks, operator);

        // 13. 返回流程实例ID - 作为后续操作的唯一标识
        return businessId;
    }

    /**
     * 更新业务数据状态
     * @param record 业务记录
     * @param flow 审批流程
     * @param firstNode 首个审批节点
     * @param flowInstanceCode 流程实例编码
     * @param userNum 操作人
     * @return 更新后的业务记录
     */
    private ChangeRecord updateBusinessDataStatus(ChangeRecord record, ApprovalFlow flow, ApprovalNode firstNode,
                                                 String flowInstanceCode, String userNum) {
        // 设置审批流程相关字段
        record.setFlowId(flow.getFlowId());
        record.setCurrentNodeId(firstNode.getNodeId());
        // 设置current_status为"审批中-节点1"
        record.setCurrentStatus(String.format("审批中-节点%d", firstNode.getNodeOrder()));
        record.setApprovalInstanceId(flowInstanceCode);
        record.setApprovalStatus(ApprovalStatus.PENDING_NODE1.getStatusName());
        record.setSubmitTime(LocalDateTime.now());
        record.setUpdatedBy(userNum);

        // 更新业务数据
        int updateCount = changeRecordMapper.updateById(record);
        if (updateCount == 0) {
            throw new RuntimeException("更新业务数据状态失败");
        }

        return record;
    }

    /**
     * 创建首个节点的待办任务
     * @param record 业务记录
     * @param flow 审批流程
     * @param firstNode 首个审批节点
     * @param approvers 审批人列表
     * @param flowInstanceCode 流程实例编码
     * @param userNum 操作人
     * @return 创建的任务列表
     */
    private List<ApprovalTask> createFirstNodeTasks(ChangeRecord record, ApprovalFlow flow, ApprovalNode firstNode,
                                                  List<String> approvers, String flowInstanceCode, String userNum) {
        List<ApprovalTask> createdTasks = new ArrayList<>();

        // 为每个审批人生成待办任务
        for (String approverNum : approvers) {
            ApprovalTask task = new ApprovalTask();
            task.setTaskId(generateTaskId());
            task.setFlowId(flow.getFlowId());
            task.setNodeId(firstNode.getNodeId());
            task.setBusinessType(flow.getBusinessType());
            task.setBusinessId(record.getId());
            task.setBusinessCode(record.getRecordCode());
            task.setApproverNum(approverNum);
            task.setApproverName(userService.getUserByUserNum(approverNum).getUserName());
            task.setTaskStatus("PENDING");
            task.setCurrentStatus(record.getCurrentStatus());
            task.setAssignTime(LocalDateTime.now());
            task.setCreatedBy(userNum);
            task.setUpdatedBy(userNum);

            // 插入任务记录
            approvalTaskMapper.insert(task);
            createdTasks.add(task);

            // 记录审批日志
            saveApprovalLog(task, "SUBMIT", userNum + "|" + userService.getUserByUserNum(userNum).getUserName(),
                          "提交审批", "", record.getCurrentStatus());
        }

        return createdTasks;
    }

    /**
     * 发送审批启动通知
     * @param record 业务记录
     * @param tasks 创建的任务列表
     * @param operator 操作人
     */
    private void sendApprovalStartNotification(ChangeRecord record, List<ApprovalTask> tasks, String operator) {
        try {
            // 构造通知内容
            Map<String, Object> notifyContent = new HashMap<>();
            notifyContent.put("businessId", record.getId());
            notifyContent.put("businessType", "CHANGE_RECORD");
            notifyContent.put("businessCode", record.getRecordCode());
            notifyContent.put("title", "新的审批任务");
            notifyContent.put("content", String.format("您有一条新的变更记录审批任务，请及时处理。"));
            notifyContent.put("operator", operator);
            notifyContent.put("createTime", LocalDateTime.now());

            // 为每个审批人发送通知
            for (ApprovalTask task : tasks) {
                notifyContent.put("approverNum", task.getApproverNum());
                notifyService.sendApprovalNotification(notifyContent);
            }
        } catch (Exception e) {
            // 通知发送失败不影响主流程，仅记录日志
            saveOpLog(operator, "NOTIFY", "ApprovalTask", record.getId(), "FAIL",
                    "审批通知发送失败: " + e.getMessage(), "", "", "");
        }
    }

    /**
     * 解析审批人列表
     * @param node 审批节点
     * @param businessId 业务ID
     * @param userNum 操作人
     * @return 解析后的审批人列表
     */
    private List<String> resolveApprovers(ApprovalNode node, Long businessId, String userNum) {
        /**
         * 审批人解析逻辑
         * 支持用户ID、角色编码、部门编码等多种指定方式
         * 数据来源于sys_user、sys_role、sys_dept表
         */
        Set<String> approversSet = new HashSet<>();

        // 1. 支持按用户ID直接指定
        if ("USER".equals(node.getApproverType())) {
            approversSet.add(node.getApproverNum());
        }
        // 2. 支持按角色编码指定
        else if (node.getApproverType().equals("ROLE")) {
            List<String> roleApprovers = userService.getUserNumsByRoleCode(node.getApproverNum());
            approversSet.addAll(roleApprovers);
        }
        // 3. 支持按部门编码指定
        else if (node.getApproverType().equals("DEPT")) {
            List<String> deptApprovers = userService.getUserNumsByDeptCode(node.getApproverNum());
            approversSet.addAll(deptApprovers);
        }
        // 4. 支持按职位编码指定
        else if (node.getApproverType().equals("POSITION")) {
            List<String> positionApprovers = userService.getUserNumsByPositionCode(node.getApproverNum());
            approversSet.addAll(positionApprovers);
        }

        // 5. 去重并移除无效用户
        List<String> validApprovers = new ArrayList<>();
        for (String approverNum : approversSet) {
            if (userService.isValidUser(approverNum)) {
                validApprovers.add(approverNum);
            }
        }

        return validApprovers;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveTask(final Long taskId, final String remark, final String userNum) {
        /**
         * 审批同意操作方法
         * @param taskId 任务ID
         * @param remark 审批意见
         * @param userNum 操作人用户编号
         * @return 操作结果
         * @throws IllegalArgumentException 参数异常
         * @throws RuntimeException 系统异常
         */
        // 1. 参数验证 - 严格检查所有必填参数
        if (taskId == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (remark == null || remark.trim().isEmpty()) {
            throw new IllegalArgumentException("审批意见不能为空");
        }
        if (userNum == null || userNum.trim().isEmpty()) {
            throw new IllegalArgumentException("操作人用户编号不能为空");
        }

        // 2. 查询审批任务 - 获取完整的任务信息
        LambdaQueryWrapper<ApprovalTask> taskQuery = new LambdaQueryWrapper<>();
        taskQuery.eq(ApprovalTask::getTaskId, String.valueOf(taskId))
                 .eq(ApprovalTask::getIsDeleted, 0);
        ApprovalTask task = approvalTaskMapper.selectOne(taskQuery);
        if (task == null) {
            throw new RuntimeException("审批任务不存在或已被删除");
        }

        // 3. 审批权限双重验证机制
        // 3.1 第一重：验证处理人为当前节点的合法审批人
        if (!userNum.equals(task.getApproverNum())) {
            throw new RuntimeException("当前用户不是该审批任务的指定审批人");
        }
        // 3.2 第二重：通过权限服务接口确认处理人权限
        boolean hasPermission = permissionService.hasApprovalPermission(userNum, task.getNodeId(), task.getBusinessType());
        if (!hasPermission) {
            throw new RuntimeException("当前用户无权限处理该审批任务");
        }

        // 4. 查询变更记录 - 获取业务数据
        ChangeRecord record = changeRecordMapper.selectById(task.getBusinessId());
        if (record == null) {
            throw new RuntimeException("关联的业务数据不存在");
        }

        // 5. 任务状态更新事务 - 确保状态变更的原子性和一致性
        updateTaskStatus(task, "已完成", remark, userNum);

        // 6. 查询当前节点配置 - 获取节点的流转规则
        ApprovalNode currentNode = approvalNodeMapper.selectOne(new LambdaQueryWrapper<ApprovalNode>()
                .eq(ApprovalNode::getNodeId, task.getNodeId())
                .eq(ApprovalNode::getIsActive, 1)
                .eq(ApprovalNode::getIsDeleted, 0));
        if (currentNode == null) {
            throw new RuntimeException("审批节点配置不存在");
        }

        // 7. 智能流程流转判断逻辑
        String changeBeforeStatus = record.getCurrentStatus();

        // 8. 查询下一个审批节点 - 根据当前节点结果决定流转
        ApprovalNode nextNode = getNextNode(task.getFlowId(), task.getNodeId());

        if (nextNode != null) {
            // 9. 有下一节点：流转至下一节点
            processNextNode(task, currentNode, nextNode, record, remark, userNum, changeBeforeStatus);
        } else {
            // 10. 没有下一节点：流程结束
            completeApprovalProcess(task, record, remark, userNum, changeBeforeStatus);
        }

        return true;
    }

    /**
     * 更新任务状态
     * @param task 审批任务
     * @param status 新状态
     * @param remark 审批意见
     * @param userNum 操作人
     */
    private void updateTaskStatus(ApprovalTask task, String status, String remark, String userNum) {
        /**
         * 任务状态更新事务
         * 确保状态变更的原子性和一致性
         */
        task.setTaskStatus(status);
        task.setApprovalTime(LocalDateTime.now());
        task.setApprovalRemark(remark);
        task.setUpdatedBy(userNum);

        int updateCount = approvalTaskMapper.updateById(task);
        if (updateCount == 0) {
            throw new RuntimeException("更新审批任务状态失败");
        }
    }

    /**
     * 处理下一节点
     * @param currentTask 当前任务
     * @param currentNode 当前节点
     * @param nextNode 下一节点
     * @param record 业务记录
     * @param remark 审批意见
     * @param userNum 操作人
     * @param changeBeforeStatus 变更前状态
     */
    private void processNextNode(ApprovalTask currentTask, ApprovalNode currentNode,
                               ApprovalNode nextNode, ChangeRecord record,
                               String remark, String userNum, String changeBeforeStatus) {
        /**
         * 处理下一节点逻辑
         * 1. 更新业务数据状态
         * 2. 解析下一节点审批人
         * 3. 创建下一节点待办任务
         * 4. 记录日志
         * 5. 发送通知
         */
        // 1. 更新业务数据状态 - 流转至下一节点
        String nextNodeStatus = String.format("审批中-节点%d", nextNode.getNodeOrder());
        record.setCurrentNodeId(nextNode.getNodeId());
        record.setCurrentStatus(nextNodeStatus);
        record.setApprovalStatus(ApprovalStatus.PENDING.name());
        record.setApprovalTime(LocalDateTime.now());
        record.setApprovalRemark(remark);
        record.setUpdatedBy(userNum);

        // 2. 根据sys_field_permission表配置控制可修改字段
        // 这里简化处理，实际应通过PermissionService获取可修改字段列表
        // String[] editableFields = permissionService.getEditableFields(userNum, "ChangeRecord", record.getCurrentStatus());

        // 3. 更新业务数据
        int updateCount = changeRecordMapper.updateById(record);
        if (updateCount == 0) {
            throw new RuntimeException("更新业务数据状态失败");
        }

        // 4. 解析下一节点审批人
        List<String> nextApprovers = resolveApprovers(nextNode, record.getId(), userNum);
        if (nextApprovers.isEmpty()) {
            throw new RuntimeException("未找到下一节点的有效审批人");
        }

        // 5. 创建下一节点待办任务
        List<ApprovalTask> nextTasks = new ArrayList<>();
        for (String approverNum : nextApprovers) {
            ApprovalTask nextTask = new ApprovalTask();
            nextTask.setTaskId(generateTaskId());
            nextTask.setFlowId(currentTask.getFlowId());
            nextTask.setNodeId(nextNode.getNodeId());
            nextTask.setBusinessType(currentTask.getBusinessType());
            nextTask.setBusinessId(currentTask.getBusinessId());
            nextTask.setBusinessCode(currentTask.getBusinessCode());
            nextTask.setApproverNum(approverNum);
            nextTask.setApproverName(userService.getUserNameByNum(approverNum));
            nextTask.setTaskStatus("PENDING");
            nextTask.setCurrentStatus(record.getCurrentStatus());
            nextTask.setAssignTime(LocalDateTime.now());
            nextTask.setCreatedBy(userNum);
            nextTask.setUpdatedBy(userNum);

            approvalTaskMapper.insert(nextTask);
            nextTasks.add(nextTask);
        }

        // 6. 双日志记录机制
        String operator = userNum + "|" + userService.getUserNameByNum(userNum);
        saveHistory(record, "APPROVE", operator, "审批通过");
        saveOpLog(operator, "APPROVE", "ChangeRecord", record.getId(), "OK", "审批通过", "", "", "");
        saveApprovalLog(currentTask, "APPROVE", operator, "审批通过",
                      changeBeforeStatus, record.getCurrentStatus());

        // 7. 发送下一节点审批通知
        sendNextNodeNotification(record, nextTasks, operator);
    }

    /**
     * 完成审批流程
     * @param task 当前任务
     * @param record 业务记录
     * @param remark 审批意见
     * @param userNum 操作人
     * @param changeBeforeStatus 变更前状态
     */
    private void completeApprovalProcess(ApprovalTask task, ChangeRecord record,
                                        String remark, String userNum, String changeBeforeStatus) {
        /**
         * 审批流程结束处理
         * 1. 更新业务数据状态为已审批
         * 2. 记录日志
         * 3. 发送通知
         */
        // 1. 更新业务数据状态 - 审批完成
        record.setCurrentStatus("已审批");
        record.setApprovalStatus(ApprovalStatus.APPROVED.name());
        record.setApproverNum(task.getApproverNum());
        record.setApproverName(task.getApproverName());
        record.setApprovalTime(task.getApprovalTime());
        record.setApprovalRemark(remark);
        record.setUpdatedBy(userNum);

        // 2. 更新业务数据
        int updateCount = changeRecordMapper.updateById(record);
        if (updateCount == 0) {
            throw new RuntimeException("更新业务数据状态失败");
        }

        // 3. 双日志记录机制
        String operator = userNum + "|" + userService.getUserNameByNum(userNum);
        saveHistory(record, "APPROVE", operator, "审批完成");
        saveOpLog(operator, "APPROVE", "ChangeRecord", record.getId(), "OK", "审批完成", "", "", "");
        saveApprovalLog(task, "APPROVE", operator, "审批完成",
                      changeBeforeStatus, record.getCurrentStatus());

        // 4. 发送审批完成通知
        sendApprovalCompleteNotification(record, operator);
    }

    /**
     * 发送下一节点审批通知
     * @param record 业务记录
     * @param nextTasks 下一节点任务列表
     * @param operator 操作人
     */
    private void sendNextNodeNotification(ChangeRecord record, List<ApprovalTask> nextTasks, String operator) {
        try {
            // 构造通知内容
            Map<String, Object> notifyContent = new HashMap<>();
            notifyContent.put("businessId", record.getId());
            notifyContent.put("businessType", "CHANGE_RECORD");
            notifyContent.put("businessCode", record.getRecordCode());
            notifyContent.put("title", "新的审批任务");
            notifyContent.put("content", String.format("您有一条新的变更记录审批任务，请及时处理。"));
            notifyContent.put("operator", operator);
            notifyContent.put("createTime", LocalDateTime.now());

            // 为每个审批人发送通知
            for (ApprovalTask task : nextTasks) {
                notifyContent.put("approverNum", task.getApproverNum());
                notifyService.sendApprovalNotification(notifyContent);
            }
        } catch (Exception e) {
            // 通知发送失败不影响主流程，仅记录日志
            saveOpLog(operator, "NOTIFY", "ApprovalTask", record.getId(), "FAIL",
                    "下一节点审批通知发送失败: " + e.getMessage(), "", "", "");
        }
    }

    /**
     * 发送审批完成通知
     * @param record 业务记录
     * @param operator 操作人
     */
    private void sendApprovalCompleteNotification(ChangeRecord record, String operator) {
        try {
            // 构造通知内容
            Map<String, Object> notifyContent = new HashMap<>();
            notifyContent.put("businessId", record.getId());
            notifyContent.put("businessType", "CHANGE_RECORD");
            notifyContent.put("businessCode", record.getRecordCode());
            notifyContent.put("title", "审批流程完成通知");
            notifyContent.put("content", String.format("变更记录审批流程已完成，审批结果：已通过。"));
            notifyContent.put("operator", operator);
            notifyContent.put("createTime", LocalDateTime.now());

            // 发送给业务创建人
            notifyContent.put("approverNum", record.getCreatedBy());
            notifyService.sendApprovalNotification(notifyContent);
        } catch (Exception e) {
            // 通知发送失败不影响主流程，仅记录日志
            saveOpLog(operator, "NOTIFY", "ApprovalTask", record.getId(), "FAIL",
                    "审批完成通知发送失败: " + e.getMessage(), "", "", "");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectTask(final Long taskId, final String remark, final String userNum, final String rejectToNode) {
        // 1. 参数验证
        if (taskId == null || remark == null || userNum == null) {
            return false;
        }

        // 2. 查询审批任务
        LambdaQueryWrapper<ApprovalTask> taskQuery = new LambdaQueryWrapper<>();
        taskQuery.eq(ApprovalTask::getTaskId, String.valueOf(taskId))
                 .eq(ApprovalTask::getIsDeleted, 0);
        ApprovalTask task = approvalTaskMapper.selectOne(taskQuery);
        if (task == null) {
            return false;
        }

        // 3. 权限验证：操作人必须是任务指定的审批人
        if (!userNum.equals(task.getApproverNum())) {
            return false;
        }

        // 4. 查询变更记录
        ChangeRecord record = changeRecordMapper.selectById(task.getBusinessId());
        if (record == null) {
            return false;
        }

        // 5. 更新任务状态
        task.setTaskStatus("REJECTED");
        task.setApprovalTime(LocalDateTime.now());
        task.setApprovalRemark(remark);
        task.setUpdatedBy(userNum);
        approvalTaskMapper.updateById(task);

        // 6. 更新变更记录状态
        record.setCurrentStatus("已拒绝");
        record.setApprovalStatus("REJECTED");
        record.setRejectReason(remark);
        record.setRejectNodeId(task.getNodeId());
        record.setApproverNum(task.getApproverNum());
        record.setApproverName(task.getApproverName());
        record.setApprovalTime(task.getApprovalTime());
        record.setApprovalRemark(remark);
        record.setUpdatedBy(userNum);
        changeRecordMapper.updateById(record);

        // 7. 记录操作日志
        String operator = userNum + "|" + userNum;
        saveHistory(record, "REJECT", operator, "审批拒绝");
        saveOpLog(operator, "REJECT", "ChangeRecord", record.getId(), "OK", "审批拒绝", "", "", "");
        saveApprovalLog(task, "REJECT", operator, "审批拒绝", task.getCurrentStatus(), record.getCurrentStatus());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferTask(final Long taskId, final String nextAssigneeNum, final String remark, final String userNum) {
        // 1. 参数验证
        if (taskId == null || nextAssigneeNum == null || userNum == null) {
            return false;
        }

        // 2. 查询审批任务
        LambdaQueryWrapper<ApprovalTask> taskQuery = new LambdaQueryWrapper<>();
        taskQuery.eq(ApprovalTask::getTaskId, String.valueOf(taskId))
                 .eq(ApprovalTask::getIsDeleted, 0);
        ApprovalTask task = approvalTaskMapper.selectOne(taskQuery);
        if (task == null) {
            return false;
        }

        // 3. 权限验证：操作人必须是任务指定的审批人
        if (!userNum.equals(task.getApproverNum())) {
            return false;
        }

        // 4. 更新任务状态
        task.setApproverNum(nextAssigneeNum);
        task.setApproverName(nextAssigneeNum); // 简化处理，实际应从用户服务获取用户名
        task.setTaskStatus("TRANSFERRED");
        task.setApprovalTime(LocalDateTime.now());
        task.setApprovalRemark(remark);
        task.setUpdatedBy(userNum);
        approvalTaskMapper.updateById(task);

        // 5. 创建新的待办任务给转办人
        ApprovalTask newTask = new ApprovalTask();
        newTask.setTaskId(generateTaskId());
        newTask.setFlowId(task.getFlowId());
        newTask.setNodeId(task.getNodeId());
        newTask.setBusinessType(task.getBusinessType());
        newTask.setBusinessId(task.getBusinessId());
        newTask.setBusinessCode(task.getBusinessCode());
        newTask.setApproverNum(nextAssigneeNum);
        newTask.setApproverName(nextAssigneeNum); // 简化处理
        newTask.setTaskStatus("PENDING");
        newTask.setCurrentStatus(task.getCurrentStatus());
        newTask.setAssignTime(LocalDateTime.now());
        newTask.setCreatedBy(userNum);
        newTask.setUpdatedBy(userNum);
        approvalTaskMapper.insert(newTask);

        // 6. 记录操作日志
        String operator = userNum + "|" + userNum;
        saveApprovalLog(task, "TRANSFER", operator, "转办审批", task.getCurrentStatus(), task.getCurrentStatus());
        saveApprovalLog(newTask, "ASSIGN", operator, "分配审批任务", task.getCurrentStatus(), task.getCurrentStatus());

        return true;
    }

    @Override
    public Page<ApprovalTask> queryTodoTasks(final String userNum, final String businessType, final int page, final int size) {
        LambdaQueryWrapper<ApprovalTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalTask::getApproverNum, userNum)
                 .eq(ApprovalTask::getTaskStatus, "PENDING")
                 .eq(ApprovalTask::getIsDeleted, 0);

        if (businessType != null && !businessType.isEmpty()) {
            queryWrapper.eq(ApprovalTask::getBusinessType, businessType);
        }

        queryWrapper.orderByDesc(ApprovalTask::getAssignTime);
        return approvalTaskMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    @Override
    public Page<ApprovalTask> queryProcessedTasks(final String userNum, final String businessType, final int page, final int size) {
        LambdaQueryWrapper<ApprovalTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalTask::getApproverNum, userNum)
                 .in(ApprovalTask::getTaskStatus, "APPROVED", "REJECTED", "TRANSFERRED")
                 .eq(ApprovalTask::getIsDeleted, 0);

        if (businessType != null && !businessType.isEmpty()) {
            queryWrapper.eq(ApprovalTask::getBusinessType, businessType);
        }

        queryWrapper.orderByDesc(ApprovalTask::getApprovalTime);
        return approvalTaskMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    @Override
    public Page<ApprovalTask> queryCompletedTasks(final String userNum, final String businessType, final int page, final int size) {
        LambdaQueryWrapper<ApprovalTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalTask::getBusinessType, "CHANGE_RECORD")
                 .in(ApprovalTask::getTaskStatus, "APPROVED", "REJECTED")
                 .eq(ApprovalTask::getIsDeleted, 0);

        if (businessType != null && !businessType.isEmpty()) {
            queryWrapper.eq(ApprovalTask::getBusinessType, businessType);
        }

        queryWrapper.orderByDesc(ApprovalTask::getApprovalTime);
        return approvalTaskMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    /**
     * 获取下一个审批节点
     */
    private ApprovalNode getNextNode(String flowId, String currentNodeId) {
        // 1. 获取当前节点
        ApprovalNode currentNode = approvalNodeMapper.selectOne(new LambdaQueryWrapper<ApprovalNode>()
                .eq(ApprovalNode::getNodeId, currentNodeId)
                .eq(ApprovalNode::getIsActive, 1)
                .eq(ApprovalNode::getIsDeleted, 0));
        if (currentNode == null) {
            return null;
        }

        // 2. 获取下一个节点（节点顺序+1）
        return approvalNodeMapper.selectOne(new LambdaQueryWrapper<ApprovalNode>()
                .eq(ApprovalNode::getFlowId, flowId)
                .eq(ApprovalNode::getNodeOrder, currentNode.getNodeOrder() + 1)
                .eq(ApprovalNode::getIsActive, 1)
                .eq(ApprovalNode::getIsDeleted, 0));
    }

    /**
     * 创建下一个节点的审批任务
     */
    private void createNextTask(ApprovalTask currentTask, ApprovalNode nextNode, ChangeRecord record, String operator) {
        ApprovalTask nextTask = new ApprovalTask();
        nextTask.setTaskId(generateTaskId());
        nextTask.setFlowId(currentTask.getFlowId());
        nextTask.setNodeId(nextNode.getNodeId());
        nextTask.setBusinessType(currentTask.getBusinessType());
        nextTask.setBusinessId(currentTask.getBusinessId());
        nextTask.setBusinessCode(currentTask.getBusinessCode());
        nextTask.setApproverNum(nextNode.getApproverNum());
        nextTask.setApproverName(nextNode.getApproverName());
        nextTask.setTaskStatus("PENDING");
        nextTask.setCurrentStatus(record.getCurrentStatus());
        nextTask.setAssignTime(LocalDateTime.now());
        nextTask.setCreatedBy(operator);
        nextTask.setUpdatedBy(operator);
        approvalTaskMapper.insert(nextTask);

        // 记录审批日志
        saveApprovalLog(nextTask, "ASSIGN", operator, "分配审批任务", record.getCurrentStatus(), record.getCurrentStatus());
    }

    /**
     * 生成任务ID
     */
    private String generateTaskId() {
        return "TASK" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()) + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 记录审批日志
     */
    private void saveApprovalLog(ApprovalTask task, String operationType, String operator, String operationRemark, String beforeStatus, String afterStatus) {
        ApprovalLog log = new ApprovalLog();
        log.setLogId("LOG" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()) + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        log.setTaskId(task.getTaskId());
        log.setFlowId(task.getFlowId());
        log.setNodeId(task.getNodeId());
        log.setBusinessType(task.getBusinessType());
        log.setBusinessId(task.getBusinessId());
        log.setBusinessCode(task.getBusinessCode());
        log.setOperationType(operationType);

        // 解析操作人信息
        String[] operatorParts = operator.split("\\|");
        if (operatorParts.length == 2) {
            log.setOperatorNum(operatorParts[0]);
            log.setOperatorName(operatorParts[1]);
        }

        log.setOperationTime(LocalDateTime.now());
        log.setOperationRemark(operationRemark);
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(afterStatus);
        log.setCreatedBy(operator);
        approvalLogMapper.insert(log);
    }

    /**
     * 记录操作日志
     */
    private void saveOpLog(String operator, String type, String objType, Long objId, String result, String msg, String pagePath, String buttonName, String ip) {
        OperationLog log = new OperationLog();
        log.setOperator(operator);
        log.setOperationType(type);
        log.setObjectType(objType);
        log.setObjectId(objId);
        log.setResult(result);
        log.setMessage(msg);
        log.setOperationTime(LocalDateTime.now());
        log.setPagePath(pagePath);
        log.setButtonName(buttonName);
        log.setIpAddress(ip);
        operationLogMapper.insert(log);
    }
}

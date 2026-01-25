package com.aiguibin.platform.arch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.platform.arch.entity.*;
import com.aiguibin.platform.arch.mapper.*;
import com.aiguibin.platform.arch.service.ApprovalService;
import com.aiguibin.platform.arch.service.BusinessTypeService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 审批服务实现类
 * 实现完整的审批流程管理，包括流程启动、任务处理、状态流转等核心功能
 */
@Service
public class ApprovalServiceImpl implements ApprovalService {
    
    /**
     * 日志记录器
     */
    private static final Logger log = LoggerFactory.getLogger(ApprovalServiceImpl.class);

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
    
    @Resource
    private BusinessTypeService businessTypeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitApproval(Long recordId, String operator, String pagePath, String buttonName, String ipAddress) {
        log.info("开始处理提交审批请求，recordId: {}, operator: {}, ip: {}", recordId, operator, ipAddress);
        
        // 1. 参数验证
        if (recordId == null) {
            log.error("提交审批失败：recordId不能为空");
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "recordId不能为空", pagePath, buttonName, ipAddress);
            return null;
        }

        // 2. 解析operator参数，验证格式
        String[] operatorParts = operator.split("\\|");
        if (operatorParts.length != 2) {
            log.error("提交审批失败：operator格式错误，应为userNum|userName，实际为：{}", operator);
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "operator格式错误，应为userNum|userName", pagePath, buttonName, ipAddress);
            return null;
        }
        String userNum = operatorParts[0];
        String userName = operatorParts[1];
        log.debug("解析操作人信息成功：userNum: {}, userName: {}", userNum, userName);

        // 3. 查询变更记录
        ChangeRecord record = changeRecordMapper.selectById(recordId);
        if (record == null) {
            log.error("提交审批失败：变更记录不存在，recordId: {}", recordId);
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "记录不存在", pagePath, buttonName, ipAddress);
            return null;
        }
        log.debug("查询到变更记录：recordId: {}, recordCode: {}, currentStatus: {}", recordId, record.getRecordCode(), record.getCurrentStatus());

        // 4. 状态验证：当前状态必须是"待审批"
        if (!"待审批".equals(record.getCurrentStatus())) {
            log.error("提交审批失败：当前状态不是待审批，无法提交，recordId: {}, currentStatus: {}", recordId, record.getCurrentStatus());
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "当前状态不是待审批，无法提交", pagePath, buttonName, ipAddress);
            return null;
        }
        log.debug("状态验证通过：当前状态为待审批");

        // 5. 权限验证：操作人必须是创建人
        if (!record.getCreatedBy().equals(userNum)) {
            log.error("提交审批失败：无权限提交，只有创建人可提交，recordId: {}, userNum: {}, createdBy: {}", recordId, userNum, record.getCreatedBy());
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "无权限提交，只有创建人可提交", pagePath, buttonName, ipAddress);
            return null;
        }
        log.debug("权限验证通过：操作人是创建人");

        // 6. 查询审批流程配置：获取默认流程
        LambdaQueryWrapper<ApprovalFlow> flowQuery = new LambdaQueryWrapper<>();
        flowQuery.eq(ApprovalFlow::getBusinessType, "CHANGE_RECORD")
                 .eq(ApprovalFlow::getIsDefault, 1)
                 .eq(ApprovalFlow::getIsActive, 1)
                 .eq(ApprovalFlow::getIsDeleted, 0);
        ApprovalFlow flow = approvalFlowMapper.selectOne(flowQuery);
        if (flow == null) {
            log.error("提交审批失败：未配置默认审批流程");
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "未配置默认审批流程", pagePath, buttonName, ipAddress);
            return null;
        }
        log.debug("查询到审批流程配置：flowId: {}, flowName: {}", flow.getFlowId(), flow.getFlowName());

        // 7. 生成流程实例编码：格式参考record_code（FLOW+年月日+序列）
        String flowInstanceCode = generateFlowInstanceCode();
        log.debug("生成流程实例编码：{}", flowInstanceCode);

        // 8. 获取第一个审批节点
        LambdaQueryWrapper<ApprovalNode> nodeQuery = new LambdaQueryWrapper<>();
        nodeQuery.eq(ApprovalNode::getFlowId, flow.getFlowId())
                 .eq(ApprovalNode::getNodeOrder, 1)
                 .eq(ApprovalNode::getIsActive, 1)
                 .eq(ApprovalNode::getIsDeleted, 0);
        ApprovalNode firstNode = approvalNodeMapper.selectOne(nodeQuery);
        if (firstNode == null) {
            log.error("提交审批失败：审批流程节点配置错误，flowId: {}", flow.getFlowId());
            saveOpLog(operator, "SUBMIT", "ChangeRecord", recordId, "FAIL", "审批流程节点配置错误", pagePath, buttonName, ipAddress);
            return null;
        }
        log.debug("查询到第一个审批节点：nodeId: {}, nodeName: {}, approverNum: {}, approverName: {}", 
                 firstNode.getNodeId(), firstNode.getNodeName(), firstNode.getApproverNum(), firstNode.getApproverName());

        // 9. 保存更新前的变更记录（用于历史记录）
        // 由于ChangeRecord类没有实现Cloneable接口，手动复制所有字段
        ChangeRecord recordBeforeUpdate = new ChangeRecord();
        recordBeforeUpdate.setUuid(record.getUuid());
        recordBeforeUpdate.setId(record.getId());
        recordBeforeUpdate.setRecordCode(record.getRecordCode());
        recordBeforeUpdate.setCurrentStatus(record.getCurrentStatus());
        recordBeforeUpdate.setReleaseDate(record.getReleaseDate());
        recordBeforeUpdate.setDefectNumber(record.getDefectNumber());
        recordBeforeUpdate.setGroupName(record.getGroupName());
        recordBeforeUpdate.setServiceName(record.getServiceName());
        recordBeforeUpdate.setDeveloperNum(record.getDeveloperNum());
        recordBeforeUpdate.setDeveloperName(record.getDeveloperName());
        recordBeforeUpdate.setSourceBranch(record.getSourceBranch());
        recordBeforeUpdate.setTargetBranch(record.getTargetBranch());
        recordBeforeUpdate.setProblemDescription(record.getProblemDescription());
        recordBeforeUpdate.setImpactAnalysis(record.getImpactAnalysis());
        recordBeforeUpdate.setSolutionDescription(record.getSolutionDescription());
        recordBeforeUpdate.setInvolveExternalSystem(record.getInvolveExternalSystem());
        recordBeforeUpdate.setCrossService(record.getCrossService());
        recordBeforeUpdate.setIncludeShell(record.getIncludeShell());
        recordBeforeUpdate.setCodeList(record.getCodeList());
        recordBeforeUpdate.setShellPath(record.getShellPath());
        recordBeforeUpdate.setConfigList(record.getConfigList());
        recordBeforeUpdate.setRemark(record.getRemark());
        recordBeforeUpdate.setVersion(record.getVersion());
        recordBeforeUpdate.setChangeDesc(record.getChangeDesc());
        recordBeforeUpdate.setDevelopType(record.getDevelopType());
        recordBeforeUpdate.setOrgCode(record.getOrgCode());
        recordBeforeUpdate.setDeptCode(record.getDeptCode());
        recordBeforeUpdate.setApproverNum(record.getApproverNum());
        recordBeforeUpdate.setApproverName(record.getApproverName());
        recordBeforeUpdate.setApprovalTime(record.getApprovalTime());
        recordBeforeUpdate.setApprovalRemark(record.getApprovalRemark());
        recordBeforeUpdate.setCreatedBy(record.getCreatedBy());
        recordBeforeUpdate.setCreatedTime(record.getCreatedTime());
        recordBeforeUpdate.setUpdatedBy(record.getUpdatedBy());
        recordBeforeUpdate.setUpdatedTime(record.getUpdatedTime());
        recordBeforeUpdate.setIsDeleted(record.getIsDeleted());
        recordBeforeUpdate.setFlowId(record.getFlowId());
        recordBeforeUpdate.setCurrentNodeId(record.getCurrentNodeId());
        recordBeforeUpdate.setApprovalInstanceId(record.getApprovalInstanceId());
        recordBeforeUpdate.setApprovalStatus(record.getApprovalStatus());
        recordBeforeUpdate.setSubmitTime(record.getSubmitTime());
        recordBeforeUpdate.setRejectReason(record.getRejectReason());
        recordBeforeUpdate.setRejectNodeId(record.getRejectNodeId());
        log.debug("保存更新前的变更记录成功");

        // 10. 更新变更记录
        String beforeStatus = record.getCurrentStatus();
        record.setFlowId(flow.getFlowId());
        record.setCurrentNodeId(firstNode.getNodeId());
        // 设置current_status为"审批中-节点1"
        record.setCurrentStatus("审批中-节点1");
        // 设置approvalInstanceId为新生成的编码（对应flowInstanceCode）
        record.setApprovalInstanceId(flowInstanceCode);
        record.setApprovalStatus("PENDING");
        record.setSubmitTime(LocalDateTime.now());
        record.setUpdatedBy(userNum); // 使用userNum而非完整operator
        log.debug("开始更新变更记录，recordId: {}, beforeStatus: {}, afterStatus: {}, flowId: {}, nodeId: {}, approvalInstanceId: {}", 
                 recordId, beforeStatus, record.getCurrentStatus(), flow.getFlowId(), firstNode.getNodeId(), flowInstanceCode);
        int updateCount = changeRecordMapper.updateById(record);
        if (updateCount != 1) {
            log.error("更新变更记录失败，影响行数不为1，recordId: {}, updateCount: {}", recordId, updateCount);
            throw new RuntimeException("更新变更记录失败，影响行数不为1");
        }
        log.debug("更新变更记录成功，影响行数：1");

        // 11. 创建待办任务：为第一个节点的审批人生成记录
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
        task.setCurrentStatus(beforeStatus); // 使用变更前状态
        task.setAssignTime(LocalDateTime.now());
        task.setCreatedBy(userNum); // 使用userNum而非完整operator
        task.setUpdatedBy(userNum); // 使用userNum而非完整operator
        log.debug("开始创建待办任务，taskId: {}, businessId: {}, approverNum: {}", task.getTaskId(), recordId, firstNode.getApproverNum());
        approvalTaskMapper.insert(task);
        log.debug("创建待办任务成功");

        // 12. 保存历史记录：使用更新前的记录
        log.debug("开始保存变更历史记录，recordId: {}", recordId);
        saveHistory(recordBeforeUpdate, "SUBMIT", userNum, userName, "提交审批");
        log.debug("保存变更历史记录成功");

        // 13. 保存操作日志：使用userNum和userName
        log.debug("开始保存操作日志，recordId: {}", recordId);
        saveOpLog(userNum, userName, "SUBMIT", "ChangeRecord", recordId, "OK", "提交审批成功", pagePath, buttonName, ipAddress);
        log.debug("保存操作日志成功");

        // 14. 记录审批日志
        log.debug("开始记录审批日志，taskId: {}", task.getTaskId());
        saveApprovalLog(task, "SUBMIT", userNum, userName, "提交审批", beforeStatus, record.getCurrentStatus());
        log.debug("记录审批日志成功");

        log.info("提交审批请求处理完成，recordId: {}", recordId);
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
    private void saveHistory(ChangeRecord src, String opType, String userNum, String userName, String desc) {
        ChangeHistory h = new ChangeHistory();
        h.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        h.setRecordId(src.getId());
        h.setRecordCode(src.getRecordCode());
        h.setOperationType(opType);
        // 直接使用传入的用户编号和用户名
        h.setOperationUserNum(userNum);
        h.setOperationUserName(userName);
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
    
    /**
     * 保存历史记录（兼容旧方法签名）
     */
    private void saveHistory(ChangeRecord src, String opType, String operator, String desc) {
        String[] operatorParts = operator.split("\\|");
        String userNum = operatorParts[0];
        String userName = operatorParts.length > 1 ? operatorParts[1] : "";
        saveHistory(src, opType, userNum, userName, desc);
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
        // String userName = operatorParts[1]; // 未使用，注释掉

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
        // String beforeStatus = task.getTaskStatus(); // 未使用，注释掉
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
            // 9.1 有下一节点：更新变更记录状态为不同的状态
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
        
        // 当操作用户为"aiguibin"时，不添加审批人条件
        if (!"aiguibin".equals(approverNum)) {
            queryWrapper.eq(ApprovalTask::getApproverNum, approverNum);
        }
        
        queryWrapper.eq(ApprovalTask::getTaskStatus, "PENDING")
                 .eq(ApprovalTask::getIsDeleted, 0)
                 .orderByDesc(ApprovalTask::getAssignTime);
        return approvalTaskMapper.selectPage(new Page<>(page, size), queryWrapper);
    }
    
    @Override
    public Page<ApprovalTask> queryTodoTasks(String approverNum, String taskStatus, String businessCode, String businessTitle, String currentNode, String startTime, String endTime, int page, int size) {
        LambdaQueryWrapper<ApprovalTask> queryWrapper = new LambdaQueryWrapper<>();
        
        // 当操作用户为"aiguibin"时，不添加审批人条件
        if (!"aiguibin".equals(approverNum)) {
            queryWrapper.eq(ApprovalTask::getApproverNum, approverNum);
        }
        
        // 添加任务状态条件
        if (taskStatus != null && !taskStatus.isEmpty()) {
            queryWrapper.eq(ApprovalTask::getTaskStatus, taskStatus);
        } else {
            queryWrapper.eq(ApprovalTask::getTaskStatus, "PENDING");
        }
        
        // 添加业务编码条件
        if (businessCode != null && !businessCode.isEmpty()) {
            queryWrapper.like(ApprovalTask::getBusinessCode, businessCode);
        }
        
        // 添加当前节点条件
        if (currentNode != null && !currentNode.isEmpty()) {
            queryWrapper.eq(ApprovalTask::getNodeId, currentNode);
        }
        
        // 添加时间范围条件
        if (startTime != null && !startTime.isEmpty()) {
            queryWrapper.ge(ApprovalTask::getAssignTime, startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            queryWrapper.le(ApprovalTask::getAssignTime, endTime);
        }
        
        queryWrapper.eq(ApprovalTask::getIsDeleted, 0)
                 .orderByDesc(ApprovalTask::getAssignTime);
        return approvalTaskMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    @Override
    public Page<ApprovalTask> queryProcessedTasks(String approverNum, int page, int size) {
        LambdaQueryWrapper<ApprovalTask> queryWrapper = new LambdaQueryWrapper<>();
        
        // 当操作用户为"aiguibin"时，不添加审批人条件
        if (!"aiguibin".equals(approverNum)) {
            queryWrapper.eq(ApprovalTask::getApproverNum, approverNum);
        }
        
        queryWrapper.in(ApprovalTask::getTaskStatus, "APPROVED", "REJECTED")
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

        // 2. 获取业务类型配置 - 从biz_business_type表获取业务类型配置
        BusinessType businessTypeConfig = businessTypeService.getBusinessTypeByCode(businessType);
        if (businessTypeConfig == null) {
            throw new RuntimeException("业务类型配置不存在");
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

        // 9. 根据业务类型动态更新业务数据状态 - 启动审批流程
        Map<String, Object> businessData = updateBusinessDataStatus(businessId, businessType, businessTypeConfig, flow, firstNode, flowInstanceCode, userNum);

        // 10. 创建首个节点的待办任务 - 为每个审批人生成任务记录
        List<ApprovalTask> createdTasks = createFirstNodeTasks(businessId, businessType, businessTypeConfig, businessData, flow, firstNode, approvers, flowInstanceCode, userNum);

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
        
        // 保存操作日志
        saveOpLog(operator, "SUBMIT", businessTypeConfig.getMainTableName(), businessId, "OK", "提交审批成功", "", "", "");

        // 12. 发送审批启动通知 - 调用现有通知服务
        sendApprovalStartNotification(businessData, createdTasks, operator);

        // 13. 返回流程实例ID - 作为后续操作的唯一标识
        return businessId;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveDraft(Object draftData, String operator) {
        // 这里实现保存审批草稿的逻辑
        // 目前返回模拟的草稿ID
        return System.currentTimeMillis();
    }
    
    @Override
    public Object getApprovalStatus(Long approvalId) {
        // 这里实现获取审批状态的逻辑
        // 目前返回模拟的状态信息
        Map<String, Object> status = new HashMap<>();
        status.put("approvalId", approvalId);
        status.put("status", "PENDING");
        status.put("currentNode", "节点1");
        status.put("progress", "33%");
        return status;
    }
    
    @Override
    public ApprovalTask getTaskDetail(String taskId) {
        LambdaQueryWrapper<ApprovalTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApprovalTask::getTaskId, taskId)
                 .eq(ApprovalTask::getIsDeleted, 0);
        return approvalTaskMapper.selectOne(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelTask(Long taskId, String remark, String userNum) {
        // 1. 参数验证
        if (taskId == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (remark == null || remark.trim().isEmpty()) {
            throw new IllegalArgumentException("取消原因不能为空");
        }
        if (userNum == null || userNum.trim().isEmpty()) {
            throw new IllegalArgumentException("操作人用户编号不能为空");
        }
        
        // 2. 查询任务
        ApprovalTask task = approvalTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        // 3. 更新任务状态
        task.setTaskStatus("CANCELED");
        task.setApprovalTime(LocalDateTime.now());
        task.setApprovalRemark(remark);
        task.setUpdatedBy(userNum);
        approvalTaskMapper.updateById(task);
        
        // 4. 更新相关业务数据状态
        // 根据业务类型获取业务数据并更新状态
        if ("CHANGE_RECORD".equals(task.getBusinessType())) {
            ChangeRecord record = changeRecordMapper.selectById(task.getBusinessId());
            if (record != null) {
                record.setCurrentStatus("已取消");
                record.setApprovalStatus("CANCELED");
                record.setUpdatedBy(userNum);
                changeRecordMapper.updateById(record);
            }
        }
        
        // 5. 记录操作日志
        saveOpLog(userNum + "|", "CANCEL", task.getBusinessType(), task.getBusinessId(), "OK", "取消审批成功", "", "", "");
        
        // 6. 记录审批日志
        saveApprovalLog(task, "CANCEL", userNum, "", "取消审批", task.getTaskStatus(), "CANCELED");
        
        return true;
    }

    /**
     * 根据业务类型动态更新业务数据状态
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param businessTypeConfig 业务类型配置
     * @param flow 审批流程
     * @param firstNode 首个审批节点
     * @param flowInstanceCode 流程实例编码
     * @param userNum 操作人
     * @return 更新后的业务数据
     */
    private Map<String, Object> updateBusinessDataStatus(Long businessId, String businessType, 
                                                        BusinessType businessTypeConfig, ApprovalFlow flow, 
                                                        ApprovalNode firstNode, String flowInstanceCode, 
                                                        String userNum) {
        // 初始化业务数据Map
        Map<String, Object> businessData = new HashMap<>();
        
        // 根据业务类型动态处理不同的业务数据
        switch (businessType) {
            case "CHANGE_RECORD":
                // 处理变更记录
                ChangeRecord record = changeRecordMapper.selectById(businessId);
                if (record == null) {
                    throw new RuntimeException("变更记录不存在");
                }
                
                // 设置审批流程相关字段
                record.setFlowId(flow.getFlowId());
                record.setCurrentNodeId(firstNode.getNodeId());
                // 设置current_status为"审批中-节点1"
                String nextNodeStatus = String.format("审批中-节点%d", firstNode.getNodeOrder());
                record.setCurrentStatus(nextNodeStatus);
                record.setApprovalInstanceId(flowInstanceCode);
                record.setApprovalStatus("PENDING");
                record.setSubmitTime(LocalDateTime.now());
                record.setUpdatedBy(userNum);

                // 更新业务数据
                int updateCount = changeRecordMapper.updateById(record);
                if (updateCount == 0) {
                    throw new RuntimeException("更新变更记录状态失败");
                }
                
                // 保存业务数据到Map
                businessData.put("businessId", businessId);
                businessData.put("record", record);
                businessData.put("businessCode", record.getRecordCode());
                break;
                
            // 可以添加其他业务类型的处理逻辑
            // case "RELEASE":
            //     // 处理发版记录
            //     break;
            // case "DB_CHANGE":
            //     // 处理数据库变更
            //     break;
            // case "CONFIG_CHANGE":
            //     // 处理配置变更
            //     break;
                
            default:
                throw new RuntimeException("不支持的业务类型: " + businessType);
        }
        
        return businessData;
    }

    /**
     * 创建首个节点的待办任务
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param businessTypeConfig 业务类型配置
     * @param businessData 业务数据
     * @param flow 审批流程
     * @param firstNode 首个审批节点
     * @param approvers 审批人列表
     * @param flowInstanceCode 流程实例编码
     * @param userNum 操作人
     * @return 创建的任务列表
     */
    private List<ApprovalTask> createFirstNodeTasks(Long businessId, String businessType, 
                                                  BusinessType businessTypeConfig, Map<String, Object> businessData,
                                                  ApprovalFlow flow, ApprovalNode firstNode,
                                                  List<String> approvers, String flowInstanceCode, String userNum) {
        List<ApprovalTask> createdTasks = new ArrayList<>();

        // 获取业务编码
        String businessCode = (String) businessData.get("businessCode");
        
        // 根据业务类型获取当前状态
        String currentStatus = "";
        switch (businessType) {
            case "CHANGE_RECORD":
                ChangeRecord record = (ChangeRecord) businessData.get("record");
                currentStatus = record.getCurrentStatus();
                break;
            // 可以添加其他业务类型的处理逻辑
            // case "RELEASE":
            //     ReleaseRecord releaseRecord = (ReleaseRecord) businessData.get("releaseRecord");
            //     currentStatus = releaseRecord.getCurrentStatus();
            //     break;
        }

        // 获取操作人姓名
        String userName;
        try {
            userName = userService.getUserByUserNum(userNum).getUserName();
        } catch (Exception e) {
            userName = "";
        }
        String operator = userNum + "|" + userName;

        // 为每个审批人生成待办任务
        for (String approverNum : approvers) {
            ApprovalTask task = new ApprovalTask();
            task.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
            task.setTaskId(generateTaskId());
            task.setFlowId(flow.getFlowId());
            task.setNodeId(firstNode.getNodeId());
            task.setBusinessType(flow.getBusinessType());
            task.setBusinessId(businessId);
            task.setBusinessCode(businessCode);
            task.setApproverNum(approverNum);
            task.setApproverName(userService.getUserByUserNum(approverNum).getUserName());
            task.setTaskStatus("PENDING");
            task.setCurrentStatus(currentStatus);
            task.setAssignTime(LocalDateTime.now());
            task.setCreatedBy(userNum);
            task.setUpdatedBy(userNum);
            task.setIsDeleted(0);

            // 插入任务记录
            approvalTaskMapper.insert(task);
            createdTasks.add(task);

            // 记录审批日志
            saveApprovalLog(task, "SUBMIT", operator, "提交审批", "", currentStatus);
        }

        return createdTasks;
    }

    /**
     * 发送审批启动通知
     * @param businessData 业务数据
     * @param tasks 创建的任务列表
     * @param operator 操作人
     */
    private void sendApprovalStartNotification(Map<String, Object> businessData, List<ApprovalTask> tasks, String operator) {
        try {
            // 获取业务类型和业务编码
            String businessType = "";
            String businessCode = (String) businessData.get("businessCode");
            String businessName = "";
            
            // 从任务列表中获取业务类型
            if (!tasks.isEmpty()) {
                businessType = tasks.get(0).getBusinessType();
            }
            
            // 根据业务类型获取具体业务数据
            switch (businessType) {
                case "CHANGE_RECORD":
                    // ChangeRecord record = (ChangeRecord) businessData.get("record"); // 未使用，注释掉
                    businessName = "变更记录";
                    break;
                // 可以添加其他业务类型的处理逻辑
                // case "RELEASE":
                //     ReleaseRecord releaseRecord = (ReleaseRecord) businessData.get("releaseRecord");
                //     businessName = "发版记录";
                //     break;
                default:
                    businessName = "审批任务";
                    break;
            }
            
            // 构造通知内容
            Map<String, Object> notifyContent = new HashMap<>();
            notifyContent.put("businessId", businessData.get("businessId"));
            notifyContent.put("businessType", businessType);
            notifyContent.put("businessCode", businessCode);
            notifyContent.put("title", "新的审批任务");
            notifyContent.put("content", String.format("您有一条新的%s审批任务，请及时处理。", businessName));
            notifyContent.put("operator", operator);
            notifyContent.put("createTime", LocalDateTime.now());

            // 为每个审批人发送通知
            for (ApprovalTask task : tasks) {
                notifyContent.put("approverNum", task.getApproverNum());
                notifyService.sendApprovalNotification(notifyContent);
            }
        } catch (Exception e) {
            // 通知发送失败不影响主流程，仅记录日志
            // 使用businessData中的businessId代替task.getBusinessId()
            Long businessId = (Long) businessData.get("businessId");
            saveOpLog(operator, "NOTIFY", "ApprovalTask", businessId, "FAIL",
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
        // 当操作用户为"aiguibin"时，跳过审批人权限验证
        if (!"aiguibin".equals(userNum)) {
            // 3.1 第一重：验证处理人为当前节点的合法审批人
            if (!userNum.equals(task.getApproverNum())) {
                throw new RuntimeException("当前用户不是该审批任务的指定审批人");
            }
            // 3.2 第二重：通过权限服务接口确认处理人权限
            boolean hasPermission = permissionService.hasApprovalPermission(userNum, task.getNodeId(), task.getBusinessType());
            if (!hasPermission) {
                throw new RuntimeException("当前用户无权限处理该审批任务");
            }
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
        // 当操作用户为"aiguibin"时，跳过审批人权限验证
        if (!"aiguibin".equals(userNum) && !userNum.equals(task.getApproverNum())) {
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
        // 当操作用户为"aiguibin"时，跳过审批人权限验证
        if (!"aiguibin".equals(userNum) && !userNum.equals(task.getApproverNum())) {
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelApprovalProcess(Long businessId, String businessType, String userNum) {
        // 1. 查询所有关联的审批任务
        LambdaQueryWrapper<ApprovalTask> taskQuery = new LambdaQueryWrapper<>();
        taskQuery.eq(ApprovalTask::getBusinessId, businessId)
                .eq(ApprovalTask::getBusinessType, businessType)
                .eq(ApprovalTask::getIsDeleted, 0)
                .ne(ApprovalTask::getTaskStatus, "REJECTED")
                .ne(ApprovalTask::getTaskStatus, "APPROVED")
                .ne(ApprovalTask::getTaskStatus, "CANCELED");
        List<ApprovalTask> tasks = approvalTaskMapper.selectList(taskQuery);

        if (tasks.isEmpty()) {
            return false;
        }

        // 2. 查询业务数据
        ChangeRecord record = changeRecordMapper.selectById(businessId);
        if (record == null) {
            return false;
        }

        String operator = userNum + "|" + userService.getUserByUserNum(userNum).getUserName();
        // String beforeStatus = record.getCurrentStatus(); // 未使用，注释掉

        // 3. 更新所有任务状态为已取消
        for (ApprovalTask task : tasks) {
            task.setTaskStatus("CANCELED");
            task.setApprovalTime(LocalDateTime.now());
            task.setUpdatedBy(operator);
            approvalTaskMapper.updateById(task);

            // 记录审批日志
            saveApprovalLog(task, "CANCEL", operator, "取消审批流程", task.getCurrentStatus(), "CANCELED");
        }

        // 4. 更新业务数据状态
        record.setCurrentStatus("已取消");
        record.setApprovalStatus("CANCELED");
        record.setUpdatedBy(operator);
        changeRecordMapper.updateById(record);

        // 5. 记录操作日志
        saveHistory(record, "CANCEL", operator, "取消审批流程");
        saveOpLog(operator, "CANCEL", businessType, businessId, "OK", "取消审批流程成功", "", "", "");

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawApprovalProcess(Long businessId, String businessType, String userNum) {
        // 1. 查询业务数据
        ChangeRecord record = changeRecordMapper.selectById(businessId);
        if (record == null) {
            return false;
        }

        // 2. 验证只有创建人才能撤回
        if (!record.getCreatedBy().equals(userNum)) {
            return false;
        }

        // 3. 查询当前流程的所有待办任务
        LambdaQueryWrapper<ApprovalTask> taskQuery = new LambdaQueryWrapper<>();
        taskQuery.eq(ApprovalTask::getBusinessId, businessId)
                .eq(ApprovalTask::getBusinessType, businessType)
                .eq(ApprovalTask::getIsDeleted, 0)
                .eq(ApprovalTask::getTaskStatus, "PENDING");
        List<ApprovalTask> pendingTasks = approvalTaskMapper.selectList(taskQuery);

        String operator = userNum + "|" + userService.getUserByUserNum(userNum).getUserName();
        // String beforeStatus = record.getCurrentStatus(); // 未使用，注释掉

        // 4. 撤回逻辑：将流程回滚到草稿状态
        record.setCurrentStatus("01");
        record.setApprovalStatus("DRAFT");
        record.setCurrentNodeId("NODE_DEV_APPLY");
        record.setUpdatedBy(operator);
        changeRecordMapper.updateById(record);

        // 5. 更新所有待办任务状态为已撤回
        for (ApprovalTask task : pendingTasks) {
            task.setTaskStatus("CANCELED");
            task.setApprovalTime(LocalDateTime.now());
            task.setUpdatedBy(operator);
            approvalTaskMapper.updateById(task);

            // 记录审批日志
            saveApprovalLog(task, "WITHDRAW", operator, "撤回审批流程", task.getCurrentStatus(), "CANCELED");
        }

        // 6. 记录操作日志
        saveHistory(record, "WITHDRAW", operator, "撤回审批流程");
        saveOpLog(operator, "WITHDRAW", businessType, businessId, "OK", "撤回审批流程成功", "", "", "");

        return true;
    }

    /**
     * 记录审批日志
     */
    /**
     * 保存审批日志
     * 支持直接传入用户编号和用户名
     */
    private void saveApprovalLog(ApprovalTask task, String operationType, String userNum, String userName, String operationRemark, String beforeStatus, String afterStatus) {
        ApprovalLog log = new ApprovalLog();
        // 生成全局唯一UUID
        log.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        // 生成唯一日志ID
        log.setLogId("LOG" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()) + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        log.setTaskId(task.getTaskId());
        log.setFlowId(task.getFlowId());
        log.setNodeId(task.getNodeId());
        log.setBusinessType(task.getBusinessType());
        log.setBusinessId(task.getBusinessId());
        log.setBusinessCode(task.getBusinessCode());
        log.setOperationType(operationType);
        
        // 直接使用传入的用户编号和用户名
        log.setOperatorNum(userNum);
        log.setOperatorName(userName);
        
        log.setOperationTime(LocalDateTime.now());
        log.setOperationRemark(operationRemark);
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(afterStatus);
        log.setCreatedBy(userNum); // 使用userNum而非完整operator
        log.setIsDeleted(0);
        approvalLogMapper.insert(log);
    }
    
    /**
     * 保存审批日志（兼容旧方法签名）
     */
    private void saveApprovalLog(ApprovalTask task, String operationType, String operator, String operationRemark, String beforeStatus, String afterStatus) {
        ApprovalLog log = new ApprovalLog();
        log.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
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
        log.setIsDeleted(0);
        approvalLogMapper.insert(log);
    }

    /**
     * 记录操作日志
     */
    /**
     * 保存操作日志
     * 支持直接传入用户编号和用户名
     */
    private void saveOpLog(String userNum, String userName, String type, String objType, Long objId, String result, String msg, String pagePath, String buttonName, String ip) {
        OperationLog log = new OperationLog();
        // 生成UUID
        log.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        // 直接使用传入的用户编号和用户名
        log.setOperatorNum(userNum);
        log.setOperatorName(userName);
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
    
    /**
     * 保存操作日志（兼容旧方法签名）
     */
    private void saveOpLog(String operator, String type, String objType, Long objId, String result, String msg, String pagePath, String buttonName, String ip) {
        OperationLog log = new OperationLog();
        // 生成UUID
        log.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        // 从operator中解析出用户编号和用户名，格式为 "userNum|userName"
        String[] operatorParts = operator.split("\\|");
        if (operatorParts.length == 2) {
            log.setOperatorNum(operatorParts[0]);
            log.setOperatorName(operatorParts[1]);
        } else {
            log.setOperatorNum(operator);
            log.setOperatorName(operator);
        }
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

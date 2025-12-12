package com.aiguibin.platform.arch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.aiguibin.platform.arch.entity.*;
import com.aiguibin.platform.arch.mapper.*;
import com.aiguibin.platform.arch.service.ApprovalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 审批服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class ApprovalServiceImplTest {

    @Mock
    private ApprovalFlowMapper approvalFlowMapper;
    
    @Mock
    private ApprovalNodeMapper approvalNodeMapper;
    
    @Mock
    private ApprovalTaskMapper approvalTaskMapper;
    
    @Mock
    private ApprovalLogMapper approvalLogMapper;
    
    @Mock
    private ChangeRecordMapper changeRecordMapper;
    
    @Mock
    private ChangeHistoryMapper changeHistoryMapper;
    
    @Mock
    private OperationLogMapper operationLogMapper;
    
    @InjectMocks
    private ApprovalServiceImpl approvalService;
    
    private ChangeRecord mockChangeRecord;
    private ApprovalFlow mockFlow;
    private ApprovalNode mockFirstNode;
    private ApprovalTask mockTask;
    
    @BeforeEach
    void setUp() {
        // 初始化测试数据
        mockChangeRecord = new ChangeRecord();
        mockChangeRecord.setId(1L);
        mockChangeRecord.setCreatedBy("USER001");
        mockChangeRecord.setCurrentStatus("待审批");
        mockChangeRecord.setRecordCode("CHG202512120001");
        
        mockFlow = new ApprovalFlow();
        mockFlow.setFlowId("FLOW001");
        mockFlow.setBusinessType("CHANGE_RECORD");
        mockFlow.setIsDefault(1);
        mockFlow.setIsActive(1);
        mockFlow.setIsDeleted(0);
        
        mockFirstNode = new ApprovalNode();
        mockFirstNode.setNodeId("NODE001");
        mockFirstNode.setFlowId("FLOW001");
        mockFirstNode.setNodeOrder(1);
        mockFirstNode.setApproverNum("APPROVER001");
        mockFirstNode.setApproverName("审批人1");
        mockFirstNode.setIsActive(1);
        mockFirstNode.setIsDeleted(0);
        
        mockTask = new ApprovalTask();
        mockTask.setTaskId("TASK202512120001");
        mockTask.setFlowId("FLOW001");
        mockTask.setNodeId("NODE001");
        mockTask.setBusinessType("CHANGE_RECORD");
        mockTask.setBusinessId(1L);
        mockTask.setApproverNum("APPROVER001");
        mockTask.setTaskStatus("PENDING");
        mockTask.setIsDeleted(0);
    }
    
    /**
     * 测试正常提交审批
     */
    @Test
    void testSubmitApproval_Success() {
        // 模拟查询
        when(changeRecordMapper.selectById(1L)).thenReturn(mockChangeRecord);
        when(approvalFlowMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockFlow);
        when(approvalNodeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockFirstNode);
        
        // 执行方法
        Long result = approvalService.submitApproval(1L, "USER001|创建人", "/change-records", "提交审批", "127.0.0.1");
        
        // 验证结果
        assertEquals(1L, result);
        
        // 验证方法调用
        verify(changeRecordMapper).updateById(any(ChangeRecord.class));
        verify(approvalTaskMapper).insert(any(ApprovalTask.class));
        verify(changeHistoryMapper).insert(any(ChangeHistory.class));
        verify(operationLogMapper).insert(any(OperationLog.class));
        verify(approvalLogMapper).insert(any(ApprovalLog.class));
    }
    
    /**
     * 测试非创建人提交审批（应该失败）
     */
    @Test
    void testSubmitApproval_NotCreator() {
        // 模拟查询
        when(changeRecordMapper.selectById(1L)).thenReturn(mockChangeRecord);
        
        // 执行方法 - 非创建人提交
        Long result = approvalService.submitApproval(1L, "USER002|非创建人", "/change-records", "提交审批", "127.0.0.1");
        
        // 验证结果
        assertNull(result);
        
        // 验证没有调用更新方法
        verify(changeRecordMapper, never()).updateById(any(ChangeRecord.class));
        verify(approvalTaskMapper, never()).insert(any(ApprovalTask.class));
    }
    
    /**
     * 测试非待审批状态提交（应该失败）
     */
    @Test
    void testSubmitApproval_InvalidStatus() {
        // 修改状态为已审批
        mockChangeRecord.setCurrentStatus("已审批");
        
        // 模拟查询
        when(changeRecordMapper.selectById(1L)).thenReturn(mockChangeRecord);
        
        // 执行方法
        Long result = approvalService.submitApproval(1L, "USER001|创建人", "/change-records", "提交审批", "127.0.0.1");
        
        // 验证结果
        assertNull(result);
        
        // 验证没有调用更新方法
        verify(changeRecordMapper, never()).updateById(any(ChangeRecord.class));
        verify(approvalTaskMapper, never()).insert(any(ApprovalTask.class));
    }
    
    /**
     * 测试正常同意审批
     */
    @Test
    void testApprove_Success() {
        // 模拟查询
        when(approvalTaskMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockTask);
        when(changeRecordMapper.selectById(1L)).thenReturn(mockChangeRecord);
        when(approvalNodeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockFirstNode);
        when(approvalNodeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null); // 没有下一节点
        
        // 执行方法
        boolean result = approvalService.approve("TASK202512120001", "同意审批", "APPROVER001|审批人1", "/approval/todo", "同意", "127.0.0.1");
        
        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(approvalTaskMapper).updateById(any(ApprovalTask.class));
        verify(changeRecordMapper).updateById(any(ChangeRecord.class));
        verify(operationLogMapper).insert(any(OperationLog.class));
        verify(approvalLogMapper).insert(any(ApprovalLog.class));
    }
    
    /**
     * 测试非任务处理人同意（应该失败）
     */
    @Test
    void testApprove_NotAssignee() {
        // 模拟查询
        when(approvalTaskMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockTask);
        
        // 执行方法 - 非任务处理人审批
        boolean result = approvalService.approve("TASK202512120001", "同意审批", "APPROVER002|非审批人", "/approval/todo", "同意", "127.0.0.1");
        
        // 验证结果
        assertFalse(result);
        
        // 验证没有调用更新方法
        verify(approvalTaskMapper, never()).updateById(any(ApprovalTask.class));
        verify(changeRecordMapper, never()).updateById(any(ChangeRecord.class));
    }
    
    /**
     * 测试正常驳回审批
     */
    @Test
    void testReject_Success() {
        // 模拟查询
        when(approvalTaskMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockTask);
        when(changeRecordMapper.selectById(1L)).thenReturn(mockChangeRecord);
        
        // 执行方法
        boolean result = approvalService.reject("TASK202512120001", "驳回原因", "APPROVER001|审批人1", "/approval/todo", "驳回", "127.0.0.1");
        
        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(approvalTaskMapper).updateById(any(ApprovalTask.class));
        verify(changeRecordMapper).updateById(any(ChangeRecord.class));
        verify(operationLogMapper).insert(any(OperationLog.class));
        verify(approvalLogMapper).insert(any(ApprovalLog.class));
    }
    
    /**
     * 测试驳回时指定驳回到的节点
     */
    @Test
    void testRejectTask_WithRejectToNode() {
        // 模拟查询
        when(approvalTaskMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockTask);
        when(changeRecordMapper.selectById(1L)).thenReturn(mockChangeRecord);
        
        // 执行方法
        boolean result = approvalService.reject("TASK202512120001", "驳回原因", "APPROVER001|审批人1", "/approval/todo", "驳回", "127.0.0.1");
        
        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(approvalTaskMapper).updateById(any(ApprovalTask.class));
        verify(changeRecordMapper).updateById(any(ChangeRecord.class));
        verify(operationLogMapper).insert(any(OperationLog.class));
        verify(approvalLogMapper).insert(any(ApprovalLog.class));
    }
    
    /**
     * 测试提交审批时记录不存在
     */
    @Test
    void testSubmitApproval_RecordNotFound() {
        // 模拟记录不存在
        when(changeRecordMapper.selectById(1L)).thenReturn(null);
        
        // 执行方法
        Long result = approvalService.submitApproval(1L, "USER001|创建人", "/change-records", "提交审批", "127.0.0.1");
        
        // 验证结果
        assertNull(result);
        
        // 验证没有调用更新方法
        verify(changeRecordMapper, never()).updateById(any(ChangeRecord.class));
        verify(approvalTaskMapper, never()).insert(any(ApprovalTask.class));
    }
    
    /**
     * 测试同意审批时任务不存在
     */
    @Test
    void testApprove_TaskNotFound() {
        // 模拟任务不存在
        when(approvalTaskMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        
        // 执行方法
        boolean result = approvalService.approve("TASK202512120001", "同意审批", "APPROVER001|审批人1", "/approval/todo", "同意", "127.0.0.1");
        
        // 验证结果
        assertFalse(result);
        
        // 验证没有调用更新方法
        verify(approvalTaskMapper, never()).updateById(any(ApprovalTask.class));
        verify(changeRecordMapper, never()).updateById(any(ChangeRecord.class));
    }
    
    /**
     * 测试驳回审批时任务不存在
     */
    @Test
    void testReject_TaskNotFound() {
        // 模拟任务不存在
        when(approvalTaskMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        
        // 执行方法
        boolean result = approvalService.reject("TASK202512120001", "驳回原因", "APPROVER001|审批人1", "/approval/todo", "驳回", "127.0.0.1");
        
        // 验证结果
        assertFalse(result);
        
        // 验证没有调用更新方法
        verify(approvalTaskMapper, never()).updateById(any(ApprovalTask.class));
        verify(changeRecordMapper, never()).updateById(any(ChangeRecord.class));
    }
}

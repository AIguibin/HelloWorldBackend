package com.aiguibin.platform.arch.enums;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 审批状态机
 * 定义状态流转规则，验证状态转换是否合法
 */
public class ApprovalStateMachine {
    
    // 状态转换规则映射：当前状态 → 允许的下一个状态集合
    private static final Map<ApprovalStatus, Set<ApprovalStatus>> TRANSITION_RULES = new HashMap<>();
    
    // 静态初始化状态转换规则
    static {
        // 初始化所有状态转换规则
        initTransitionRules();
    }
    
    /**
     * 初始化状态转换规则
     */
    private static void initTransitionRules() {
        // 草稿状态可以转换到待审批-节点1或取消
        Set<ApprovalStatus> draftTransitions = new HashSet<>();
        draftTransitions.add(ApprovalStatus.PENDING_NODE1);
        draftTransitions.add(ApprovalStatus.CANCELLED);
        TRANSITION_RULES.put(ApprovalStatus.DRAFT, draftTransitions);
        
        // 待审批-节点1可以转换到待审批-节点2、已拒绝或取消
        Set<ApprovalStatus> pendingNode1Transitions = new HashSet<>();
        pendingNode1Transitions.add(ApprovalStatus.PENDING_NODE2);
        pendingNode1Transitions.add(ApprovalStatus.REJECTED);
        pendingNode1Transitions.add(ApprovalStatus.CANCELLED);
        TRANSITION_RULES.put(ApprovalStatus.PENDING_NODE1, pendingNode1Transitions);
        
        // 待审批-节点2可以转换到待审批-节点3、已拒绝或取消
        Set<ApprovalStatus> pendingNode2Transitions = new HashSet<>();
        pendingNode2Transitions.add(ApprovalStatus.PENDING_NODE3);
        pendingNode2Transitions.add(ApprovalStatus.REJECTED);
        pendingNode2Transitions.add(ApprovalStatus.CANCELLED);
        TRANSITION_RULES.put(ApprovalStatus.PENDING_NODE2, pendingNode2Transitions);
        
        // 待审批-节点3可以转换到待审批-节点4、已拒绝或取消
        Set<ApprovalStatus> pendingNode3Transitions = new HashSet<>();
        pendingNode3Transitions.add(ApprovalStatus.PENDING_NODE4);
        pendingNode3Transitions.add(ApprovalStatus.REJECTED);
        pendingNode3Transitions.add(ApprovalStatus.CANCELLED);
        TRANSITION_RULES.put(ApprovalStatus.PENDING_NODE3, pendingNode3Transitions);
        
        // 待审批-节点4可以转换到待审批-节点5、已拒绝或取消
        Set<ApprovalStatus> pendingNode4Transitions = new HashSet<>();
        pendingNode4Transitions.add(ApprovalStatus.PENDING_NODE5);
        pendingNode4Transitions.add(ApprovalStatus.REJECTED);
        pendingNode4Transitions.add(ApprovalStatus.CANCELLED);
        TRANSITION_RULES.put(ApprovalStatus.PENDING_NODE4, pendingNode4Transitions);
        
        // 待审批-节点5可以转换到已审批、已拒绝或取消
        Set<ApprovalStatus> pendingNode5Transitions = new HashSet<>();
        pendingNode5Transitions.add(ApprovalStatus.APPROVED);
        pendingNode5Transitions.add(ApprovalStatus.REJECTED);
        pendingNode5Transitions.add(ApprovalStatus.CANCELLED);
        TRANSITION_RULES.put(ApprovalStatus.PENDING_NODE5, pendingNode5Transitions);
        
        // 已拒绝状态可以转换到待审批-节点1（重新提交）或取消
        Set<ApprovalStatus> rejectedTransitions = new HashSet<>();
        rejectedTransitions.add(ApprovalStatus.PENDING_NODE1);
        rejectedTransitions.add(ApprovalStatus.CANCELLED);
        TRANSITION_RULES.put(ApprovalStatus.REJECTED, rejectedTransitions);
        
        // 已审批和已取消状态是终态，不能转换到其他状态
        TRANSITION_RULES.put(ApprovalStatus.APPROVED, new HashSet<>());
        TRANSITION_RULES.put(ApprovalStatus.CANCELLED, new HashSet<>());
    }
    
    /**
     * 验证状态转换是否合法
     * 
     * @param fromStatus 当前状态
     * @param toStatus 目标状态
     * @return true表示转换合法，false表示转换不合法
     */
    public static boolean isValidTransition(ApprovalStatus fromStatus, ApprovalStatus toStatus) {
        if (fromStatus == null || toStatus == null) {
            return false;
        }
        
        // 相同状态转换总是合法的
        if (fromStatus == toStatus) {
            return true;
        }
        
        // 检查是否在允许的转换规则中
        Set<ApprovalStatus> allowedTransitions = TRANSITION_RULES.get(fromStatus);
        return allowedTransitions != null && allowedTransitions.contains(toStatus);
    }
    
    /**
     * 根据当前状态和操作类型获取预期的下一个状态
     * 
     * @param currentStatus 当前状态
     * @param action 操作类型
     * @return 预期的下一个状态，如果操作不合法则返回null
     */
    public static ApprovalStatus getExpectedNextStatus(ApprovalStatus currentStatus, ApprovalAction action) {
        if (currentStatus == null || action == null) {
            return null;
        }
        
        switch (action) {
            case SUBMIT:
                // 提交操作：从草稿到待审批-节点1
                if (currentStatus == ApprovalStatus.DRAFT) {
                    return ApprovalStatus.PENDING_NODE1;
                }
                // 重新提交：从已拒绝到待审批-节点1
                if (currentStatus == ApprovalStatus.REJECTED) {
                    return ApprovalStatus.PENDING_NODE1;
                }
                break;
                
            case APPROVE:
                // 同意操作：进入下一个节点或完成审批
                switch (currentStatus) {
                    case PENDING_NODE1:
                        return ApprovalStatus.PENDING_NODE2;
                    case PENDING_NODE2:
                        return ApprovalStatus.PENDING_NODE3;
                    case PENDING_NODE3:
                        return ApprovalStatus.PENDING_NODE4;
                    case PENDING_NODE4:
                        return ApprovalStatus.PENDING_NODE5;
                    case PENDING_NODE5:
                        return ApprovalStatus.APPROVED;
                    default:
                        return null;
                }
                
            case REJECT:
                // 拒绝操作：任何待审批状态都可以拒绝
                if (currentStatus.name().startsWith("PENDING_")) {
                    return ApprovalStatus.REJECTED;
                }
                break;
                
            case CANCEL:
                // 取消操作：任何状态都可以取消
                return ApprovalStatus.CANCELLED;
                
            default:
                break;
        }
        
        return null;
    }
    
    /**
     * 获取当前状态允许的操作类型
     * 
     * @param currentStatus 当前状态
     * @return 允许的操作类型集合
     */
    public static Set<ApprovalAction> getAllowedActions(ApprovalStatus currentStatus) {
        if (currentStatus == null) {
            return new HashSet<>();
        }
        
        Set<ApprovalAction> allowedActions = new HashSet<>();
        
        switch (currentStatus) {
            case DRAFT:
                allowedActions.add(ApprovalAction.SUBMIT);
                allowedActions.add(ApprovalAction.DELETE);
                allowedActions.add(ApprovalAction.CANCEL);
                break;
                
            case PENDING_NODE1:
            case PENDING_NODE2:
            case PENDING_NODE3:
            case PENDING_NODE4:
            case PENDING_NODE5:
                allowedActions.add(ApprovalAction.APPROVE);
                allowedActions.add(ApprovalAction.REJECT);
                allowedActions.add(ApprovalAction.TRANSFER);
                allowedActions.add(ApprovalAction.DELETE);
                allowedActions.add(ApprovalAction.CANCEL);
                break;
                
            case REJECTED:
                allowedActions.add(ApprovalAction.SUBMIT); // 重新提交
                allowedActions.add(ApprovalAction.DELETE);
                allowedActions.add(ApprovalAction.CANCEL);
                break;
                
            case APPROVED:
            case CANCELLED:
                allowedActions.add(ApprovalAction.DELETE);
                break;
                
            default:
                break;
        }
        
        return allowedActions;
    }
    
    /**
     * 检查操作类型是否允许在当前状态下执行
     * 
     * @param currentStatus 当前状态
     * @param action 操作类型
     * @return true表示操作允许，false表示操作不允许
     */
    public static boolean isAllowedAction(ApprovalStatus currentStatus, ApprovalAction action) {
        if (currentStatus == null || action == null) {
            return false;
        }
        
        Set<ApprovalAction> allowedActions = getAllowedActions(currentStatus);
        return allowedActions.contains(action);
    }
}

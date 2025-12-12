package com.aiguibin.platform.arch.enums;

/**
 * 审批操作枚举
 * 与系统操作日志（sys_operation_log）中的操作类型保持一致
 */
public enum ApprovalAction {
    /**
     * 提交：创建变更记录后，提交审批
     */
    SUBMIT("SUBMIT", "创建变更记录后，提交审批"),
    
    /**
     * 同意：审批人同意当前节点的审批
     */
    APPROVE("APPROVE", "审批人同意当前节点的审批"),
    
    /**
     * 拒绝：审批人拒绝当前节点的审批
     */
    REJECT("REJECT", "审批人拒绝当前节点的审批"),
    
    /**
     * 转办：审批人将当前任务转交给其他审批人
     */
    TRANSFER("TRANSFER", "审批人将当前任务转交给其他审批人"),
    
    /**
     * 删除：删除审批任务
     */
    DELETE("DELETE", "删除审批任务"),
    
    /**
     * 取消：申请人取消审批流程
     */
    CANCEL("CANCEL", "申请人取消审批流程"),
    
    /**
     * 重新提交：被拒绝后，重新提交审批
     */
    RESUBMIT("RESUBMIT", "被拒绝后，重新提交审批");
    
    private final String actionName;
    private final String description;
    
    /**
     * 构造函数
     * @param actionName 操作名称
     * @param description 操作描述
     */
    ApprovalAction(String actionName, String description) {
        this.actionName = actionName;
        this.description = description;
    }
    
    /**
     * 获取操作名称
     * @return 操作名称
     */
    public String getActionName() {
        return actionName;
    }
    
    /**
     * 获取操作描述
     * @return 操作描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据操作名称获取枚举值
     * @param actionName 操作名称
     * @return 枚举值
     */
    public static ApprovalAction fromActionName(String actionName) {
        for (ApprovalAction action : values()) {
            if (action.actionName.equals(actionName)) {
                return action;
            }
        }
        return null;
    }
}

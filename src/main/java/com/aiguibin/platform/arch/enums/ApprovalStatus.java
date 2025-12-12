package com.aiguibin.platform.arch.enums;

/**
 * 审批状态枚举
 * 与字典表（sys_dict_type, sys_dict_item）中的状态定义保持一致
 */
public enum ApprovalStatus {
    /**
     * 草稿：初始状态，创建变更记录后自动设置
     */
    DRAFT("草稿", "初始状态，创建变更记录后自动设置"),
    
    /**
     * 待审批：通用待审批状态
     */
    PENDING("待审批", "通用待审批状态"),
    
    /**
     * 待审批-节点1：提交审批后，进入第一个审批节点
     */
    PENDING_NODE1("待审批-节点1", "提交审批后，进入第一个审批节点"),
    
    /**
     * 待审批-节点2：第一个节点审批通过后，进入第二个审批节点
     */
    PENDING_NODE2("待审批-节点2", "第一个节点审批通过后，进入第二个审批节点"),
    
    /**
     * 待审批-节点3：第二个节点审批通过后，进入第三个审批节点
     */
    PENDING_NODE3("待审批-节点3", "第二个节点审批通过后，进入第三个审批节点"),
    
    /**
     * 待审批-节点4：第三个节点审批通过后，进入第四个审批节点
     */
    PENDING_NODE4("待审批-节点4", "第三个节点审批通过后，进入第四个审批节点"),
    
    /**
     * 待审批-节点5：第四个节点审批通过后，进入第五个审批节点
     */
    PENDING_NODE5("待审批-节点5", "第四个节点审批通过后，进入第五个审批节点"),
    
    /**
     * 已审批：所有节点审批通过，审批流程结束
     */
    APPROVED("已审批", "所有节点审批通过，审批流程结束"),
    
    /**
     * 已拒绝：任何节点审批拒绝，审批流程结束
     */
    REJECTED("已拒绝", "任何节点审批拒绝，审批流程结束"),
    
    /**
     * 已取消：审批流程被取消
     */
    CANCELLED("已取消", "审批流程被取消");
    
    private final String statusName;
    private final String description;
    
    /**
     * 构造函数
     * @param statusName 状态名称
     * @param description 状态描述
     */
    ApprovalStatus(String statusName, String description) {
        this.statusName = statusName;
        this.description = description;
    }
    
    /**
     * 获取状态名称
     * @return 状态名称
     */
    public String getStatusName() {
        return statusName;
    }
    
    /**
     * 获取状态描述
     * @return 状态描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据状态名称获取枚举值
     * @param statusName 状态名称
     * @return 枚举值
     */
    public static ApprovalStatus fromStatusName(String statusName) {
        for (ApprovalStatus status : values()) {
            if (status.statusName.equals(statusName)) {
                return status;
            }
        }
        return null;
    }
}

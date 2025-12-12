package com.aiguibin.platform.arch.enums;

/**
 * 审批人类型枚举
 * 定义审批流程中审批人的类型
 */
public enum ApproverType {
    /**
     * 用户：具体的某个用户
     */
    USER("用户", "具体的某个用户"),
    
    /**
     * 角色：具有某个角色的所有用户
     */
    ROLE("角色", "具有某个角色的所有用户"),
    
    /**
     * 部门：部门的所有用户
     */
    DEPT("部门", "部门的所有用户"),
    
    /**
     * 职位：具有某个职位的所有用户
     */
    POSITION("职位", "具有某个职位的所有用户");
    
    private final String approverTypeName;
    private final String description;
    
    /**
     * 构造函数
     * @param approverTypeName 审批人类型名称
     * @param description 审批人类型描述
     */
    ApproverType(String approverTypeName, String description) {
        this.approverTypeName = approverTypeName;
        this.description = description;
    }
    
    /**
     * 获取审批人类型名称
     * @return 审批人类型名称
     */
    public String getApproverTypeName() {
        return approverTypeName;
    }
    
    /**
     * 获取审批人类型描述
     * @return 审批人类型描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据审批人类型名称获取枚举值
     * @param approverTypeName 审批人类型名称
     * @return 枚举值
     */
    public static ApproverType fromApproverTypeName(String approverTypeName) {
        for (ApproverType approverType : values()) {
            if (approverType.approverTypeName.equals(approverTypeName)) {
                return approverType;
            }
        }
        return null;
    }
}

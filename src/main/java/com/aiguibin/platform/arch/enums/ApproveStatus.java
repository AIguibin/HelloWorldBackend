package com.aiguibin.platform.arch.enums;

/**
 * 审批状态枚举
 */
public enum ApproveStatus {
    
    /**
     * 草稿
     */
    DRAFT("DRAFT", "草稿"),
    
    /**
     * 待审批
     */
    PENDING("PENDING", "待审批"),
    
    /**
     * 已通过
     */
    APPROVED("APPROVED", "已通过"),
    
    /**
     * 已拒绝
     */
    REJECTED("REJECTED", "已拒绝"),
    
    /**
     * 已取消
     */
    CANCELED("CANCELED", "已取消");
    
    private final String code;
    private final String name;
    
    ApproveStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * 根据编码获取枚举值
     */
    public static ApproveStatus getByCode(String code) {
        for (ApproveStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}

package com.aiguibin.platform.arch.enums;

/**
 * 执行状态枚举
 */
public enum ExecuteStatus {
    
    /**
     * 待执行
     */
    PENDING("PENDING", "待执行"),
    
    /**
     * 执行中
     */
    EXECUTING("EXECUTING", "执行中"),
    
    /**
     * 成功
     */
    SUCCESS("SUCCESS", "成功"),
    
    /**
     * 失败
     */
    FAILED("FAILED", "失败");
    
    private final String code;
    private final String name;
    
    ExecuteStatus(String code, String name) {
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
    public static ExecuteStatus getByCode(String code) {
        for (ExecuteStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}

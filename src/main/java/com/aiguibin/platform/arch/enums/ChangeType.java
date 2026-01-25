package com.aiguibin.platform.arch.enums;

/**
 * 变更类型枚举
 */
public enum ChangeType {
    
    /**
     * 新增
     */
    ADD("ADD", "新增"),
    
    /**
     * 修改
     */
    MOD("MOD", "修改"),
    
    /**
     * 删除
     */
    DEL("DEL", "删除");
    
    private final String code;
    private final String name;
    
    ChangeType(String code, String name) {
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
    public static ChangeType getByCode(String code) {
        for (ChangeType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}

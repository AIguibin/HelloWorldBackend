package com.aiguibin.platform.arch.dto;

import lombok.Data;

/**
 * 执行结果视图对象
 */
@Data
public class ExecuteResultVO {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 成功结果
     */
    public static ExecuteResultVO success() {
        ExecuteResultVO result = new ExecuteResultVO();
        result.setSuccess(true);
        result.setMessage("执行成功");
        return result;
    }
    
    /**
     * 失败结果
     */
    public static ExecuteResultVO fail(String message) {
        ExecuteResultVO result = new ExecuteResultVO();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}
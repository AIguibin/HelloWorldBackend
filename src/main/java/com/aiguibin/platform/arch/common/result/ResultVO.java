package com.aiguibin.platform.arch.common.result;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 通用响应结果包装类
 * 整合了 ExecuteResultVO 和 PageResultVO 的功能
 * 
 * @param <T> 泛型数据类型
 */
@Data
public class ResultVO<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    private String traceId;
    
    public ResultVO() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public ResultVO(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public ResultVO(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
    
    public static <T> ResultVO<T> success() {
        return new ResultVO<>(200, "操作成功");
    }
    
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(200, "操作成功", data);
    }
    
    public static <T> ResultVO<T> success(String message, T data) {
        return new ResultVO<>(200, message, data);
    }
    
    public static <T> ResultVO<T> error(Integer code, String message) {
        return new ResultVO<>(code, message);
    }
    
    public static <T> ResultVO<T> error(String message) {
        return new ResultVO<>(500, message);
    }
    
    public static <T> ResultVO<T> error(Integer code, String message, T data) {
        return new ResultVO<>(code, message, data);
    }
    
    public static <T> ResultVO<PageData<T>> page(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        PageData<T> pageData = new PageData<>(pageNum, pageSize, total, list);
        return new ResultVO<>(200, "查询成功", pageData);
    }
    
    public static <T> ResultVO<PageData<T>> page(String message, Integer pageNum, Integer pageSize, Long total, List<T> list) {
        PageData<T> pageData = new PageData<>(pageNum, pageSize, total, list);
        return new ResultVO<>(200, message, pageData);
    }
    
    public static ResultVO<ExecuteData> executeSuccess() {
        ExecuteData executeData = new ExecuteData(true, "执行成功");
        return new ResultVO<>(200, "执行成功", executeData);
    }
    
    public static ResultVO<ExecuteData> executeSuccess(String message) {
        ExecuteData executeData = new ExecuteData(true, message);
        return new ResultVO<>(200, message, executeData);
    }
    
    public static ResultVO<ExecuteData> executeFail(String message) {
        ExecuteData executeData = new ExecuteData(false, message);
        return new ResultVO<>(500, message, executeData);
    }
    
    /**
     * 分页数据对象
     * 用于封装分页查询结果
     * 
     * @param <T> 列表项数据类型
     */
    @Data
    public static class PageData<T> implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Integer pageNum;
        private Integer pageSize;
        private Long total;
        private Integer totalPages;
        private List<T> list;
        
        public PageData() {
        }
        
        public PageData(Integer pageNum, Integer pageSize, Long total, List<T> list) {
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            this.total = total;
            this.list = list;
            this.totalPages = (int) Math.ceil((double) total / pageSize);
        }
    }
    
    /**
     * 执行结果数据对象
     * 用于封装操作执行结果
     */
    @Data
    public static class ExecuteData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private boolean success;
        private String message;
        
        public ExecuteData() {
        }
        
        public ExecuteData(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}

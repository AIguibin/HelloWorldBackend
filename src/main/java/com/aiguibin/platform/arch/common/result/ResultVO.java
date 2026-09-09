package com.aiguibin.platform.arch.common.result;

import com.aiguibin.platform.arch.common.web.TraceIdFilter;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import org.slf4j.MDC;

/**
 * 统一响应结果包装类.
 *
 * <p>方法命名约定（避免重载二义性）：{@code success(data)} 只设数据、{@code successMsg(message, data)}
 * 同时设提示与数据；不要调用 {@code success("字符串")}——它会命中 data 而不是 message。
 *
 * <p>traceId 由 {@link TraceIdFilter} 写入 MDC，构造时自动带入，无需手工赋值。
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
        this.traceId = MDC.get(TraceIdFilter.TRACE_ID_KEY);
    }

    public ResultVO(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public ResultVO(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    public boolean isSuccess() {
        return this.code != null && this.code == ResultCode.SUCCESS.getCode();
    }

    /** 成功且无数据. */
    public static ResultVO<Void> success() {
        return new ResultVO<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }

    /** 成功并携带数据. */
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /** 成功并自定义提示与数据. */
    public static <T> ResultVO<T> successMsg(String message, T data) {
        return new ResultVO<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static ResultVO<Void> error(ResultCode resultCode) {
        return new ResultVO<>(resultCode.getCode(), resultCode.getMessage());
    }

    public static ResultVO<Void> error(int code, String message) {
        return new ResultVO<>(code, message);
    }

    /** 通用失败（code=500）. */
    public static ResultVO<Void> error(String message) {
        return new ResultVO<>(ResultCode.ERROR.getCode(), message);
    }

    /** 分页查询结果. */
    public static <T> ResultVO<PageData<T>> page(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        return new ResultVO<>(ResultCode.SUCCESS.getCode(), "查询成功", new PageData<>(pageNum, pageSize, total, list));
    }

    /**
     * 分页数据对象.
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

        public PageData() {}

        public PageData(Integer pageNum, Integer pageSize, Long total, List<T> list) {
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            this.total = total;
            this.list = list;
            this.totalPages = pageSize == null || pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        }
    }
}

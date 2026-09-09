package com.aiguibin.platform.arch.common.result;

/**
 * 统一响应码枚举：HTTP 语义 + 业务错误码的唯一定义处.
 * 业务模块需要细分错误码时，在本枚举扩展或按模块新增枚举（保持 code 全局唯一）。
 */
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已失效"),
    FORBIDDEN(403, "无权访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    ERROR(500, "系统异常，请稍后重试");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

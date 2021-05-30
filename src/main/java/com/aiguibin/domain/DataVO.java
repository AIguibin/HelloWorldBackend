package com.aiguibin.domain;

public class DataVO<T> {
    private int errorCode;
    private String errorMessage;
    private T data;

    /**
     * 默认的构造函数
     */
    public DataVO() {
        super();
        this.errorCode = 0;
        this.errorMessage = null;
    }

    /**
     * 默认的构造函数
     *
     * @param dataStore 数据对象
     */
    public DataVO(T dataStore) {
        this();
        this.data = dataStore;
    }

    /**
     * 默认的构造函数
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public DataVO(int errorCode, String errorMessage) {
        this();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * 设置错误代码
     *
     * @param errorCode 错误代码
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 获取返回状态，errorCode为0时表示成功，否则返回失败。为了跟老的消息格式定义兼容。
     *
     * @return 返回状态
     */
    public String getRet() {
        return this.errorCode == 0 ? "ok" : "error";
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * 设置错误消息
     *
     * @param errorMessage 错误消息
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * 返回错误消息，为了跟老的消息格式定义兼容。
     *
     * @return 错误消息
     */
    public String getMsg() {
        return this.errorMessage;
    }

    /**
     * 获取数据对象
     *
     * @return 数据对象
     */
    public T getData() {
        return this.data;
    }

    /**
     * 设置数据对象
     *
     * @param data 数据对象
     */
    public void setData(T data) {
        this.data = data;
    }
}

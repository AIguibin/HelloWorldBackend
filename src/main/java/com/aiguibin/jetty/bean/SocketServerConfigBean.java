package com.aiguibin.jetty.bean;
/**
 * 描述： 
 *
 * @author AIguibin Date time 2018/9/25 0:42
 */
public class SocketServerConfigBean extends HttpServerConfigBean {
    /**
     * 构造函数
     *
     * @param serverType 服务器类型
     * @see ServerType
     */
    public SocketServerConfigBean(ServerType serverType) {
        super(serverType);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public int getThreads() {
        return 0;
    }

    @Override
    public boolean isSecurity() {
        return false;
    }

    @Override
    public String getKeystorePath() {
        return null;
    }

    @Override
    public String getKeystorePassword() {
        return null;
    }

    @Override
    public String getKeyManagerPassword() {
        return null;
    }

    @Override
    public long getIdleTimeoutSecs() {
        return 0;
    }

    @Override
    public long getOutputSize() {
        return 0;
    }

    @Override
    public long getRequestHeaderSize() {
        return 0;
    }

    @Override
    public long getResponseHeaderSize() {
        return 0;
    }
}

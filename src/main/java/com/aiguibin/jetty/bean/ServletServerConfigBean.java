package com.aiguibin.jetty.bean;

import com.aiguibin.jetty.helper.StringHelper;
import com.aiguibin.jetty.helper.TypeHelper;
import org.springframework.beans.factory.annotation.Value;

public class ServletServerConfigBean extends HttpServerConfigBean {
    @Value("${servlet.enabled:false}")
    private boolean enabled;
    @Value("${servlet.port:9999}")
    private int port;
    @Value("${servlet.threads:100}")
    private int threads;
    @Value("${servlet.security:false}")
    private boolean security;
    @Value("${servlet.security.keystore:}")
    private String keystorePath;
    @Value("${servlet.security.keystorePassword:}")
    private String keystorePassword;
    @Value("${servlet.security.keyManagerPassword:}")
    private String keyManagerPassword;

    @Value("${servlet.idleTimeoutSecs:300}")
    private int idleTimeoutSecs;
    @Value("${servlet.outputSize:32K}")
    private String outputSizeString;
    @Value("${servlet.requestHeaderSize:8K}")
    private String requestHeaderSizeString;
    @Value("${servlet.responseHeaderSize:8K}")
    private String responseHeaderSizeString;

    @Value("${servlet.service.constant:}")
    private String serviceClasses;

    /**
     * 默认的构造函数
     */
    public ServletServerConfigBean() {
        super(ServerType.RESTful);
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getPort()
     */
    @Override
    public int getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getThreads()
     */
    @Override
    public int getThreads() {
        return threads;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#isSecurity()
     */
    @Override
    public boolean isSecurity() {
        return security;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getKeystorePath()
     */
    @Override
    public String getKeystorePath() {
        return keystorePath;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getKeystorePassword()
     */
    @Override
    public String getKeystorePassword() {
        return keystorePassword;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getKeyManagerPassword()
     */
    @Override
    public String getKeyManagerPassword() {
        return keyManagerPassword;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getIdleTimeoutSecs()
     */
    @Override
    public long getIdleTimeoutSecs() {
        return idleTimeoutSecs;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getOutputSize()
     */
    @Override
    public long getOutputSize() {
        return TypeHelper.string2Size(outputSizeString, 32 * 1024);
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getRequestHeaderSize()
     */
    @Override
    public long getRequestHeaderSize() {
        return TypeHelper.string2Size(requestHeaderSizeString, 8 * 1024);
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getResponseHeaderSize()
     */
    @Override
    public long getResponseHeaderSize() {
        return TypeHelper.string2Size(responseHeaderSizeString, 8 * 1024);
    }

    /**
     * 获取配置的Servlet服务类列表
     *
     * @return Servlet服务类类别
     */
    public String[] getServiceClasses() {
        return StringHelper.split(serviceClasses);
    }
}

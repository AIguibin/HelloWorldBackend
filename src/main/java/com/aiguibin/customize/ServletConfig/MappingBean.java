package com.aiguibin.customize.ServletConfig;
/**
 * 定义Mapping属性
 *
 * @author AIguibin
 * Date time 2019年04月26日 23:36:12
 */
public class MappingBean {
    private String servletName;
    private String url;
    private String clazz;

    public MappingBean(String servletName, String url, String clazz) {
        this.servletName = servletName;
        this.url = url;
        this.clazz = clazz;
    }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}

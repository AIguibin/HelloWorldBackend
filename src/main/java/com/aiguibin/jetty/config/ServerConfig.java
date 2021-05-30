package com.aiguibin.jetty.config;

import com.aiguibin.jetty.bean.RestfulServerConfigBean;
import com.aiguibin.jetty.bean.ServletServerConfigBean;
import com.aiguibin.jetty.factory.RestfulServerFactory;
import com.aiguibin.jetty.factory.ServletServerFactory;
import com.aiguibin.spring.config.SpringConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 描述： 这是一个web.xml
 *
 * @author AIguibin Date time 2018/8/9 0:11
 */
@Configuration
@Import(SpringConfig.class)
public class ServerConfig {
    private static final Log logger = LogFactory.getLog(ServerConfig.class);
    /**
     * 创建RESTful服务配置对象
     *
     * @return RESTful服务配置对象
     */
    @Bean
    public RestfulServerConfigBean restfulServerConfigBean() {
        return new RestfulServerConfigBean();
    }
    /**
     * 创建Servlet服务配置对象
     *
     * @return Servlet服务配置对象
     */
    @Bean
    public ServletServerConfigBean servletServerConfigBean() {
        return new ServletServerConfigBean();
    }
    /**
     * 创建RESTful服务器工厂
     *
     * @param context                 Spring IoC上下文
     * @param restfulServerConfigBean RESTful配置对象
     * @return RESTful服务器工厂
     */
    @Bean(name = "restfulServerFactory", initMethod = "init", destroyMethod = "destroy")
    public RestfulServerFactory restfulServerFactory(ApplicationContext context,
                                                     RestfulServerConfigBean restfulServerConfigBean) {
        return new RestfulServerFactory(context, restfulServerConfigBean);
    }
    /**
     * 创建Servlet服务器工厂
     *
     * @param context                 Spring IoC上下文
     * @param servletServerConfigBean Servlet服务配置对象
     * @return Servlet服务器工厂
     */
    @Bean(name = "servletServerFactory", initMethod = "init", destroyMethod = "destroy")
    public ServletServerFactory servletServerFactory(ApplicationContext context,
                                                     ServletServerConfigBean servletServerConfigBean) {
        return new ServletServerFactory(context, servletServerConfigBean);
    }
}

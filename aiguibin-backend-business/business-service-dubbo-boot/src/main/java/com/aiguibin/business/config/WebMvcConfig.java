package com.aiguibin.business.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * web初始化类,替代xml
 * 继承Abs..类后,会同时创建DispatcherServlet和ContextLoaderListener
 * 前提:Servlet3.0，如果是2.0请参考WebApplicationInitializer也就是WebXmlConfig类
 * <p>
 * Spring提供了AbstractAnnotationConfigDispatcherServletInitializer类
 * 是WebApplicationInitializer的子类，实现之即可进行Spring的servlet配置DispatcherServlet
 * 启动时,会产生一个 Spring 应用程序上下文,把它和配置文件中声明的 bean 或者类一起加载进来。
 * 通过getServletConfigClasses() 方法,设置 DispatcherServlet 通过 WebConfig 配置类来完成 Spring 上下文和 bean 的加载。
 * <p>
 * 但是在 Spring web 程序中,往往还有另外一个应用程序上下文,它是由 ContextLoaderListener 产生的。
 * 通过调用 getRootConfigClasses()方法返回的类就是用来配置 ContextLoaderListener 产生的上下文。
 * <p>
 * 其中,DispatcherServlet 是用来加载涉及 web 功能的 beans,例如 controllers, view resolvers,和 handler mappings;
 * 而 ContextLoaderListener 则是用来载入程序中其余的 beans,例如一些中间层和数据层组件,完成的是程序后端功能。
 *
 * @author AIguibin
 * Date time 2019年05月05日 15:12:05
 */
@Configuration
public class WebMvcConfig extends AbstractAnnotationConfigDispatcherServletInitializer {
    /**
     * 配置ContextLoaderListener
     * <p>
     * 通过getRootConfigClasses()方法返回的类就是用来配置 ContextLoaderListener 产生的上下文
     * 来载入程序中其余的 beans,例如一些中间层和数据层组件,完成的是程序后端功能
     * 为Servlet的根配置,配置 Spring IoC 容器的上下文配置
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{WebXmlConfig.class};
    }

    /**
     * 配置DispatcherServlet
     * <p>
     * 通过 getServletConfigClasses()方法返回的配置类来完成 Spring 上下文和 bean 的加载
     * 加载涉及 web 功能的 beans,例如 controllers, view resolvers,和 handler mappings;
     * 为DispatcherServlet的配置, 配置 DispatcherServlet 上下文配置
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebAppConfig.class};
    }

    /**
     * 配置ServletMappings
     * <p>
     * 配置 DispatcherServlet 拦截内容
     * SpringMvc拦截的URL映射==>具体拦截哪些路径
     * 如同web.xml中配置的：
     * <servlet>
     * <servlet-name>SpringMvc</servlet-name>
     * <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
     * </servlet>
     * <servlet-mapping>
     * <servlet-name>SpringMvc</servlet-name>
     * <url-pattern>/</url-pattern>
     * </servlet-mapping>
     * 此处将拦截所有请求
     * 实质是为 DispatcherServlet 提供一个或更多的 Servlet映射
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}

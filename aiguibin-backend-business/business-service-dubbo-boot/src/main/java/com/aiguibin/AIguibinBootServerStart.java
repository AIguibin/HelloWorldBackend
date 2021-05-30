package com.aiguibin;


import com.aiguibin.business.server.AIguibinTomcat;
import org.apache.catalina.LifecycleException;


public class AIguibinBootServerStart {
    public static void main(String[] args) throws  LifecycleException {
       /**
        * AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(WebMvcConfig.class);
        *         context.start();
        *         System.in.read();
        * 把项目配置到 Tomcat 服务器中，就能够启动了。就这么几行就可以启动了呢。为什么？
        * 在Servlet 3.0环境中，容器会在类路径中查找实现 javax.servlet.ServletContainerInitializer 接口的类，
        * 如果发现的话，就会用它来配置Servlet容器。
        * Spring提供了这个接口的实现，名为 org.springframework.web.SpringServletContainerInitializer ，
        * 这个类反过来又会查找实现 org.springframework.web.WebApplicationInitializer 的类并将配置的任务交给它们来完成
        * Spring 3.2 引入了一个便利的 WebApplicationInitializer 基础实现，
        * 也就是 org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer 。
        * 因为我们的 WebAppInitializer 扩展了 AbstractAnnotationConfigDispatcherServletInitializer
        * 同时也就实现了 WebApplicationInitializer ，因此当部署到 Servlet 3.0 容器中的时候，容器会自动发现它，
        * 并用它来配置Servlet上下文，所以启动Tomcat就会自动加载实现AbstractAnnotationConfigDispatcherServletInitializer的配置类
        */
        AIguibinTomcat.start();
    }
}

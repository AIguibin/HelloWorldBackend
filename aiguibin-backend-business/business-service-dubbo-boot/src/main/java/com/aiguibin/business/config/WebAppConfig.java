package com.aiguibin.business.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * SpringMvc相关配置信息
 * SpringMvc进行Web的上下文配置需要继承WebMvcConfigurer类，
 * 同时配置类也要有@Configuration，@EnableWebMvc和@ComponentScan注解
 *
 * @author AIguibin
 * Date time 2019年05月05日 14:50:53
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.aiguibin"},
        includeFilters= {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)},
        useDefaultFilters = false)
public class WebAppConfig implements WebMvcConfigurer {
    /***
     * 通过注解 @Bean 初始化视图解析器
     * @return ViewResolver 视图解析器
     */
    @Bean(name="initViewResolver")
    public ViewResolver initViewResolver() {
        InternalResourceViewResolver viewResolver =new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    /**
     * 配置静态资源的处理
     * 将请求交由Servlet处理,不经过DispatchServlet
     */
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer){
        configurer.enable();
    }
}

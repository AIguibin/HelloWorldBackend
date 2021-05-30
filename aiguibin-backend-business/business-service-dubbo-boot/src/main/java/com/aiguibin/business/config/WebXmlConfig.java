package com.aiguibin.business.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;

/**
 * 根配置信息
 * 扫描业务层，除Controller以外的
 * 如果不用SpringMvc的话可以实现 WebApplicationInitializer来配置
 * 原来 web.xml中的Servlet,filter,listener等原生的所有内容，可以完全取代。
 * 如果需要SpringMvc实现web功能这个可以配置为空的
 * WebApplicationInitializer实现类为Servlet的根配置，并指定该配置的加载顺序为所有配置之前
 * 由@Order注解控制，数值越小优先级越高
 * Spring提供了AbstractAnnotationConfigDispatcherServletInitializer类
 * 是WebApplicationInitializer的子类，实现之即可进行Spring的servlet配置
 *
 * @author AIguibin
 * Date time 2019年05月05日 14:45:50
 */

@Configuration
@ComponentScan(basePackages = {"com.aiguibin"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)})
@Import({MyBatisConfig.class, ProviderConfiguration.class})
public class WebXmlConfig{
}

package com.aiguibin.business.config;

import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author AIguibin
 * Date time 2019年05月07日 18:21:10
 */

@Configuration
public class MonitorConfig {
    //配置Druid监控 访问  http://localhost:8080/druid/login.html
    //配置一个管理后台的Servlet
    /*@Bean
    public ServletRegistrationBean statViewServlet(){  //注册一个Servlet
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String,String> initParams = new HashMap<>();

        initParams.put("loginUsername","admin");
        initParams.put("loginPassword","123456");
        initParams.put("allow","");//默认就是允许所有访问IP
        initParams.put("deny","192.168.15.21");  //拒绝访问的IP
        bean.setInitParameters(initParams);
        return bean;
    }*/

    //2.配置一个web监控的filter
    /*@Bean
    public FilterRegistrationBean webStatFilter(){//注册一个Filter
        FilterRegistrationBean bean=new FilterRegistrationBean(new WebStatFilter());
        Map<String,String> initParams = new HashMap<>();
        initParams.put("exclusions","*.js,*.css,/druid/*"); //排除的请求
        bean.setInitParameters(initParams);
        bean.setUrlPatterns(Arrays.asList("/*")); //拦截的请求
        return  bean;
    }*/
}

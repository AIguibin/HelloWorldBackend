package com.aiguibin.config;

import com.aiguibin.action.FirstRestful;
import com.aiguibin.action.FirstServlet;
import com.aiguibin.jetty.config.ServerConfig;
import com.aiguibin.spring.config.SpringConfig;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

@Configuration
@Import({
        SpringConfig.class,
        ServerConfig.class
})
@PropertySource({
        "classpath:server.properties"
})
@ComponentScan({
        "com.aiguibin.action",
})
public class RestConfig {
    @Bean(name = "restfulClasses")
    public List<Class<?>> restfulClasses() {
        return Arrays.asList(FirstRestful.class);
    }
    @Bean(name = "servletClasses")
    public List<Class<?>> servletClasses(){
        return Arrays.asList(FirstServlet.class);
    }
}

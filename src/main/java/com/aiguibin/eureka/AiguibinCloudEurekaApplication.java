package com.aiguibin.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * 描述： SpringBoot启动类,SpringCloud的注册中心
 * 创建 SpringCloud 项目： 首先选择创建 SpringBoot项目
 * 创建注册中心选择 cloud Discovery => Eureka Server
 * 创建客户端点选择 cloud Discovery => Eureka Discovery
 * 然后：修改配置文件，修改启动类添加注解，配置Security
 * @author AIguibin Date time 2019/1/3 1:33
 */
@EnableEurekaServer
@SpringBootApplication
public class AiguibinCloudEurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiguibinCloudEurekaApplication.class, args);
    }
    @EnableWebSecurity
    static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // Configure HttpSecurity as needed (e.g. enable http basic).
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
            http.csrf().disable();
            //注意：为了可以使用 http://${user}:${password}@${host}:${port}/eureka/ 这种方式登录,所以必须是httpBasic,
            // 如果是form方式,不能使用url格式登录
            http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
        }
    }
}


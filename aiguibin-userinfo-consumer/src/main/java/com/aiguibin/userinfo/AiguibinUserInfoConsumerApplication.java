package com.aiguibin.userinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 描述： spring cloud中discovery service有许多种实现（eureka、consul、zookeeper等等）。
 * --@EnableDiscoveryClient基于spring-cloud-commons,
 * --@EnableEurekaClient基于spring-cloud-netflix。
 * 如果选用的注册中心是eureka，那么就推荐@EnableEurekaClient，
 * 如果是其他的注册中心，那么推荐使用@EnableDiscoveryClient。
 *
 * @author AIguibin Date time 2019/1/7 22:15
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class AiguibinUserInfoConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiguibinUserInfoConsumerApplication.class, args);
    }
}


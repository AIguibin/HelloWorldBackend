package com.aiguibin.business.config;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableDubbo(scanBasePackages = "com.aiguibin.business.client")
@PropertySource("classpath:dubbo/dubbo-consumer.properties")
@ComponentScan(value = {"com.aiguibin.business.client"})
public class ConsumerConfiguration {
}

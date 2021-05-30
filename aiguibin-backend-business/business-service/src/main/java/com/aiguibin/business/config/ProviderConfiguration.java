package com.aiguibin.business.config;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:dubbo/dubbo-provider.properties")
@EnableDubbo(scanBasePackages = "com.aiguibin.business.service")
public class ProviderConfiguration {
}

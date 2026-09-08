package com.aiguibin.platform.arch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 架构管理平台启动类.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringbootStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootStarterApplication.class, args);
    }
}

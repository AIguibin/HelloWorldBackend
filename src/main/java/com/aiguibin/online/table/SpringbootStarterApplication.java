package com.aiguibin.online.table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringbootStarterApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringbootStarterApplication.class);

    public static void main(String[] args) {
        long strTime = System.currentTimeMillis();
        ApplicationContext context = SpringApplication.run(SpringbootStarterApplication.class, args);
        long internal = System.currentTimeMillis() - strTime;
        logger.info("启动成功~侦听端口： {} ", context.getEnvironment().getProperty("server.port", "8080"));
        logger.info("启动成功~启动时长： {} 分 {} 秒 {} 毫秒", internal / 1000 / 60, internal / 1000 % 60, internal % 1000);
    }

}
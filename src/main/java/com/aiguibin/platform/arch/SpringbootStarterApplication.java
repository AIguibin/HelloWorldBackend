package com.aiguibin.platform.arch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Spring Boot 启动类.
 * 用于启动整个应用程序，配置应用上下文等.
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class SpringbootStarterApplication extends SpringBootServletInitializer {

    /**
     * 日志记录器.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringbootStarterApplication.class);

    /**
     * 线程池核心大小.
     */
    private static final int CORE_POOL_SIZE = 10;

    /**
     * 主方法.
     * @param args 命令行参数.
     */
    public static void main(final String[] args) {
        LOGGER.info("======== 系统启动中 ========");
        SpringApplication.run(SpringbootStarterApplication.class, args);
        LOGGER.info("======== 系统启动成功 ========");
    }

    /**
     * 配置线程池.
     * @return 线程池实例.
     */
    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(CORE_POOL_SIZE);
    }
}

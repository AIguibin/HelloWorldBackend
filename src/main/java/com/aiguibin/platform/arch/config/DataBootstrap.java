package com.aiguibin.platform.arch.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据引导类，已禁用自动数据初始化
 * 数据初始化工作已通过脚本完成
 */
@Component
public class DataBootstrap implements CommandLineRunner {
    
    @Override
    public void run(String... args) {
        // 数据初始化工作已通过脚本完成，此处禁用自动数据初始化
        // 相关数据初始化逻辑已移除
    }
}
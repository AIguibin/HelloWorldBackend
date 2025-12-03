package com.aiguibin.platform.arch.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 菜单数据初始化组件，已禁用自动菜单数据初始化
 * 菜单数据初始化工作已通过脚本完成
 */
@Component
public class MenuBootstrap implements CommandLineRunner {
    
    @Override
    public void run(String... args) {
        // 菜单数据初始化工作已通过脚本完成，此处禁用自动菜单数据初始化
        // 相关菜单数据初始化逻辑已移除
    }
}
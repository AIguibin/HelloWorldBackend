package com.aiguibin.platform.arch.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import java.util.Date;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 框架配置：分页插件 + 审计字段自动填充.
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 审计字段填充：created_time / updated_time / created_by / updated_by.
     * 框架默认以 system 落库；接入认证后可改为从上下文取当前操作人。
     */
    @Bean
    public MetaObjectHandler auditMetaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                Date now = new Date();
                strictInsertFill(metaObject, "createdTime", Date.class, now);
                strictInsertFill(metaObject, "updatedTime", Date.class, now);
                strictInsertFill(metaObject, "createdBy", String.class, "system");
                strictInsertFill(metaObject, "updatedBy", String.class, "system");
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                strictUpdateFill(metaObject, "updatedTime", Date.class, new Date());
                strictUpdateFill(metaObject, "updatedBy", String.class, "system");
            }
        };
    }
}

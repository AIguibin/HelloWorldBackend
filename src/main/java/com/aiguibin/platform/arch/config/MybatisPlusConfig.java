package com.aiguibin.platform.arch.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * MyBatis-Plus 框架配置：分页插件 + 乐观锁插件 + 审计字段自动填充.
 * 对应公司建表规范 V4.0 的公共字段（CREATE_TIME/CREATE_USER/UPDATE_TIME/UPDATE_USER/VERSION）.
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁：实体 @Version 字段更新时自动校验并递增（WHERE version = ?）
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 审计字段填充（V4.0 公共字段）：CREATE_TIME / UPDATE_TIME / CREATE_USER / UPDATE_USER.
     * 操作人默认 system；接入认证后改为取当前登录用户。VERSION 由乐观锁插件维护，
     * DEL_IND 由 @TableLogic 维护，均不在此填充。
     */
    @Bean
    public MetaObjectHandler auditMetaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                Date now = new Date();
                strictInsertFill(metaObject, "createTime", Date.class, now);
                strictInsertFill(metaObject, "updateTime", Date.class, now);
                strictInsertFill(metaObject, "createUser", String.class, "system");
                strictInsertFill(metaObject, "updateUser", String.class, "system");
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
                strictUpdateFill(metaObject, "updateUser", String.class, "system");
            }
        };
    }
}

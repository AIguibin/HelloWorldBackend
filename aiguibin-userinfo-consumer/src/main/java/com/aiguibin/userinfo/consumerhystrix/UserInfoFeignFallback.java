package com.aiguibin.userinfo.consumerhystrix;

import com.aiguibin.userinfo.consumerfeign.UserInfoFeignClient;
import org.springframework.stereotype.Component;


/**
 * 描述： 异常处理机制
 * 首先是依赖 Hystrix
 * 此处由于我们依了 Feign 组件已经把 Hystrix 已经包含进来了
 * 我们需要的就是在 @FeignClient(name = "aiguibin-userinfo-provider",
 *                            fallback = UserInfoFeignFallback.class)
 *                            的注解中加上Fallback的指定的实现类，
 *                            并且让实现类被spring管理就OK了
 * @author AIguibin Date time 2019/1/11 22:38
 */
@Component
public class UserInfoFeignFallback implements UserInfoFeignClient {
    @Override
    public String login() {
        return "用户登录失败,异常处理";
    }
}

package com.aiguibin.userinfo.consumerfeign;

import com.aiguibin.userinfo.consumerhystrix.UserInfoFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 描述：
 *
 * @author AIguibin Date time 2019/1/7 23:06
 */
@Component
@FeignClient(name = "aiguibin-userinfo-provider",fallback = UserInfoFeignFallback.class)
public interface UserInfoFeignClient {
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    String login();
}

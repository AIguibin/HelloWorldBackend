package com.aiguibin.userinfo.consumecontroller;

import com.aiguibin.userinfo.consumerfeign.UserInfoFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
/**
 * 描述：
 *
 * @author AIguibin Date time 2019/1/7 23:14
 */

@RefreshScope
@RestController
public class LoginController {

    // @Value("${test}")
    // private String register;
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    public LoginController(UserInfoFeignClient userInfoFeignClient) {
        this.userInfoFeignClient = userInfoFeignClient;
    }
    @RequestMapping(value = "/userLogin",method = RequestMethod.GET)
    public String userLogin(){
        // System.out.println(register);
        return userInfoFeignClient.login();
    }
}

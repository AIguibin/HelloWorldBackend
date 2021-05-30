package com.aiguibin.action;

import com.aiguibin.entities.UserInfoEntity;
import com.aiguibin.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 描述： 用户登陆
 *
 * @author AIguibin Date time 2018/12/23 0:07
 */
@Controller
public class UserLoginController {
    @Autowired
    private UserInfoService userInfoService;


    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(){
        System.out.println("登录成功");
        return "success";
    }

    @RequestMapping(value = "/grap", method = RequestMethod.GET)
    public String grap(){
        System.out.println("进入红包页");
        return "grap";
    }
    @RequestMapping(value = "/insert",method = RequestMethod.GET)
    public String insert(){
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setAddress(3);
        userInfoEntity.setAge(22);
        userInfoEntity.setBirthday(new Date());
       // userInfoEntity.setId((long) 2018);
        userInfoEntity.setRegister(System.currentTimeMillis());
        userInfoEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        userInfoEntity.setUserName("贵斌");
        int i = userInfoService.addUserInfo(userInfoEntity);
        if (i>0){
            System.out.println("保存成功");
            return "success";
        }else {
            System.out.println("保存失败");
            return "success";
        }
    }
    @RequestMapping(value = "/update",method = RequestMethod.GET)
    public String update(){
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setId((long) 2);
        userInfoEntity.setAddress(5);
        userInfoEntity.setAge(5);
        userInfoEntity.setBirthday(new Date());
        // userInfoEntity.setId((long) 2018);
        userInfoEntity.setRegister(System.currentTimeMillis());
        userInfoEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        userInfoEntity.setUserName("贵斌1");
        int i = userInfoService.updateUser(userInfoEntity);
        if (i>0){
            System.out.println("修改成功");
            return "success";
        }else {
            System.out.println("修改失败");
            return "success";
        }
    }
    @RequestMapping(value = "/selectUser",method = RequestMethod.GET)
    public String selectUser(){
        List<UserInfoEntity> entities = userInfoService.selectUser();
        return "success";
    }
}

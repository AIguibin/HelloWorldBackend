package com.aiguibin.springboot.redis.controller;


import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
public class HelloWorldController {
    private static final Logger log=LoggerFactory.getLogger(HelloWorldController.class);

    @RequestMapping(value = "firstMeetYou",method = RequestMethod.GET)
   /* public HelloWorldEntity firstMeetYou(){
        HelloWorldEntity helloWorldEntity = new HelloWorldEntity();
        helloWorldEntity.setMessage("你好，世界！");
        return helloWorldEntity;
    }*/
    public String firstMeetYou(){
        return "你好，世界！";
    }


    @RequestMapping(value = "/uploadMultipartFile",method = RequestMethod.POST)
    public void uploaded(@RequestPart(value = "file")   MultipartFile[] multifile, @RequestParam(value="params") String params) {
        System.out.println("进来了");
    }
}

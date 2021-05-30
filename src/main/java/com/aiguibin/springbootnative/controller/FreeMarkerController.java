package com.aiguibin.springbootnative.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author AIguibin
 * Date time 2019年04月06日 20:12:25
 */
@Controller
@RequestMapping(value = "/freemarker")
public class FreeMarkerController {
    //日志声明
    private static final Log logger = LogFactory.getLog(FreeMarkerController.class);

    @RequestMapping(value = "/index")
    public String freeMarke(Map<String, Object> map){
        map.put("name", "Joe");
        map.put("sex", 1);    //sex:性别，1：男；0：女；

        // 模拟数据
        List<Map<String, Object>> friends = new ArrayList<Map<String, Object>>();
        Map<String, Object> friend = new HashMap<String, Object>();
        friend.put("name", "xbq");
        friend.put("age", 22);
        friends.add(friend);
        friend = new HashMap<String, Object>();
        friend.put("name", "July");
        friend.put("age", 18);
        friends.add(friend);
        map.put("friends", friends);
        return "freemarker";
    }
}

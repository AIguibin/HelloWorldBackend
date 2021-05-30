package com.aiguibin.action;

import com.aiguibin.service.UserRedPacketService;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 *
 * @author AIguibin Date time 2018/12/20 1:17
 */
@Log4j2
@Controller
@RequestMapping("/userRedPacket")
public class UserRedPacketController {
    private static Logger logger = LogManager.getLogger(UserRedPacketController.class);
    @Autowired
    private UserRedPacketService userRedPacketService;

    @RequestMapping(value = "/grapRedPacket")
    @ResponseBody
    public Map<String, Object> grapRedPacket(Long redPacketId, Long userId) {
        // 抢红包
        int result = userRedPacketService.grapRedPacket(redPacketId, userId);
        Map<String, Object> retMap = new HashMap<String, Object>();
        boolean flag = result > 0;
        retMap.put("success", flag);
        retMap.put("message", flag ? "抢红包成功" : "抢红包失败");
        return retMap;
    }

}

package com.aiguibin.action;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path("rest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FirstRestful {


    @Path("first")
    @GET
    public JSONObject data(){
        JSONObject data=new JSONObject();
        data.put("one","hello");
        data.put("two","world");
        return data;
    }
}

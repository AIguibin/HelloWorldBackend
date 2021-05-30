package com.aiguibin.webservice.server;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * 供外部调用的接口实现
 *
 * @author AIguibin
 * Date time 2019年04月23日 17:09:13
 */
@WebService
public class HelloWebServiceImpl implements HelloWebService{

     /**
      * 供客户端调用方法  该方法是非静态的，会被发布
      * @param name  传入参数
      * @return String 返回结果
      */
     @Override
     @WebMethod
     public String getName(String name) {
          return "欢迎你！ "+name;
     }

     /**
      * 无论继承实现与否都不影响发布
      * @author AIguibin
      * Date time 2019/4/23 22:08
      * @return String
      */
     public String getValue(String name){
          return "欢迎你！ "+name;
     }
     /**
      * 方法上加@WebMentod(exclude=true)后，此方法不被发布；
      */
     @WebMethod(exclude=true)
     public String getHello(String name){
          return "你好！ "+name;
     }

     /**
      * 静态方法不会被发布
      */
     public static String getString(String name){
          return "再见！"+name;
     }

}

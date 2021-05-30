package com.aiguibin.webservice.server;

import javax.jws.WebService;

/**
 * 供外部调用的接口
 *
 * @author AIguibin
 * Date time 2019年04月23日 17:09:13
 */
public interface HelloWebService {
     String getName(String name);
     String getHello(String name);
}

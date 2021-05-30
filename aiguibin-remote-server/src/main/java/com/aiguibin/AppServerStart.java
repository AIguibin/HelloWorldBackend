package com.aiguibin;

import com.aiguibin.server.ServerCenter;
import com.aiguibin.server.ServerCenterImpl;
import com.aiguibin.service.HelloWorldService;
import com.aiguibin.service.HelloWorldServiceImpl;

/**
 * Hello world!
 */
public class AppServerStart {
    public static void main(String[] args) {
        Thread thread=new Thread(() -> {
            ServerCenter serverCenter=new ServerCenterImpl("127.0.0.1",9999);
            serverCenter.register(HelloWorldService.class,HelloWorldServiceImpl.class);
            serverCenter.start();
        });
        thread.start();
    }

}

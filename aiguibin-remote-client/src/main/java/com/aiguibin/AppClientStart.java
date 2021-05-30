package com.aiguibin;

import com.aiguibin.client.ClientCenter;
import com.aiguibin.service.HelloWorldService;

import java.net.InetSocketAddress;

/**
 * Hello world!
 */
public class AppClientStart {
    public static void main(String[] args) {
        try {
            for (int i = 0; i < 100; i++) {
                HelloWorldService helloWorldService=ClientCenter.getRemoteProxyObject(Class.forName("com.aiguibin.service.HelloWorldService"),new InetSocketAddress("127.0.0.1",9999));
                System.out.println(helloWorldService.sayHelloName("  liuguibin"));
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

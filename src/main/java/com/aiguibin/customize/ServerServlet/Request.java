package com.aiguibin.customize.ServerServlet;

import java.io.IOException;
import java.io.InputStream;

/**
 * request对象
 *
 * @author AIguibin
 * Date time 2019年04月25日 15:19:25
 */
public class Request {
    private String url;
    private String method;

    /**
     * 构造函数
     * @author AIguibin
     * Date time 2019/4/25 15:30
     * @param inputStream 输入流
     * @return request对象
     */
    public Request(InputStream inputStream) throws IOException {
        String httpRequest = "";
        byte[] requestByte=new byte[1024];
        int length = 0;
        if ((length=inputStream.read(requestByte))>0){
            httpRequest=new String(requestByte ,0,length);
        }
        /**
         * 我们通过输入流，对 HTTP 协议进行解析，
         * 拿到了 HTTP 请求头的方法以及 URL
         */
        System.out.println(httpRequest);
        String httpHead=httpRequest.split("\n")[0];
        url=httpHead.split("\\s")[1];
        method=httpHead.split("\\s")[0];
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}

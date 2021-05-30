package com.aiguibin.customize.ServerServlet;

import java.io.IOException;
import java.io.OutputStream;

/**
 * response对象
 *
 * @author AIguibin
 * Date time 2019年04月25日 15:47:18
 */
public class Response {
    private OutputStream outputStream;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * 基于HTTP协议的格式进行输出
     * @author AIguibin
     * Date time 2019/4/25 15:52
     * @param context 返回的内容
     * @return void
     */
    public void write(String context) throws IOException {
        StringBuffer httpResponse=new StringBuffer();
        httpResponse.append("HTTP/1.1 200 OK\n")
                .append("Content-Type: text/html ; charset=UTF-8")
                .append("\r\n")
                .append("<html><body>")
                .append(context)
                .append("</body></html>");
        outputStream.write(httpResponse.toString().getBytes());
        //outputStream.close();
    }
}

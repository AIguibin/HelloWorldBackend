package com.aiguibin.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerCenterImpl implements ServerCenter {
    private static String host;
    private static int port;
    private static boolean isRunning = false;
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public ServerCenterImpl() {
    }

    public ServerCenterImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 用一个Map充当注册中心的容器，把所有提供服务的接口都注册到该容器中
     * key：接口的名字 String类型，value：接口的实现类 Class类型
     */
    private static HashMap<String, Class> registerCenter = new HashMap<>();

    /**
     * 开启服务端服务
     */
    @Override
    public void start() {
        try {
            //创建服务端ServerSocket
            ServerSocket server = new ServerSocket();
            //绑定服务主机与端口
            server.bind(new InetSocketAddress(host, port));
            //设置线程标示
            isRunning=true;
            System.out.println("Server is Running");
            while (true) {
                // ServerSocket中的accept()方法是一个线程阻塞方法,线程阻塞在这里等待客户端连接
                // 也就是如果没有客户端来连接,那么线程就一直停在那里,直到有一个客户端口连接了,才开始执行后面的代码
                Socket socket = server.accept();
                /**
                 * 每获取一个链接就去线程池拿出一个链接去处理客户端一次请求
                 * 通过构造方法把socket对象传到线程中
                 */
                executor.execute(new serviceTask(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭服务端服务
     */
    @Override
    public void stop() {
        //设置线程标示
        isRunning=false;
        executor.shutdown();
        System.out.println("Server is Shutdown");
    }

    /**
     * 注册服务
     *
     * @param service     服务接口
     * @param serviceImpl 服务接口的实现类
     */
    @Override
    public void register(Class service, Class serviceImpl) {
        //利用反射技术获取接口名称作为String类型的KEY
        registerCenter.put(service.getName(), serviceImpl);
    }


    /**
     * 线程任务
     */
    public static class serviceTask implements Runnable {
        private static Socket socket;

        public serviceTask() {
        }

        public serviceTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ObjectInputStream input = null;
            ObjectOutputStream output = null;
            try {
                /**
                 * 客户端连接上以后,通过socket接受客户端发送来的数据
                 * InputStream 字节流
                 * ObjectInputStream 对象流(序列化流)
                 */
                input = new ObjectInputStream(socket.getInputStream());
                // 因为ObjectInputStream 对收发的顺序严格要求，所以此处必须参照发送的顺序逐个接受
                String serviceName = input.readUTF();
                String methodName = input.readUTF();
                Class[] parameterTypes = (Class[]) input.readObject();
                Object[] arguments = (Object[]) input.readObject();
                // 根据客户端请求以及接收到的数据,到注册中心匹配具体的接口
                Class serviceClazz = registerCenter.get(serviceName);
                // 通过反射获取具体方法
                Method method = serviceClazz.getMethod(methodName, parameterTypes);
                // 通过反射执行执行该方法
                Object result = method.invoke(serviceClazz.newInstance(), arguments);
                // 将执行结果返回给客户端
                output = new ObjectOutputStream(socket.getOutputStream());
                output.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                    if (output != null) {
                        output.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

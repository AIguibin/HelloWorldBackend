package com.aiguibin.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;


public class ClientCenter {

    /**
     * 获取服务端接口的代理对象
     * @param serviceInterface 原本是需要接口名称，但是为了创建代理对象获取类加载器。
     *                封装为接口对象，使用的时候使用反射Class.getName()方法获取接口名称
     * @param socketAddress 远程服务地址IP:PORT
     * @param <T> 获取的代理对象的泛型
     * @return 代理对象
     */
    public static <T> T getRemoteProxyObject(Class serviceInterface, InetSocketAddress socketAddress){
        /**
         * 创建代理对象Proxy.newProxyInstance(接口类加载器,接口集合,InvocationHandler对象)
         * @param loader：一个classloader对象，定义了由哪个classloader对象对生成的代理类进行加载
         * @param interfaces：一个interface对象数组，表示我们将要给我们的代理对象提供一组什么样的接口，
         *              如果我们提供了这样一个接口对象数组，那么也就是声明了代理类实现了这些接口，
         *              代理类就可以调用接口中声明的所有方法。
         * @param handler：一个InvocationHandler对象，表示的是当动态代理对象调用方法的时候
         *           会关联到哪一个InvocationHandler对象上，并最终由其调用。
         * InvocationHandler 是一个接口，接口是抽象的不能被实例化，所以必须实现其方法
         *                  我们需要写一个类去实现它然后传过来，这里就简单粗暴直接new出来接口的实现。
         *                  最终我们的动态代理是通过InvocationHandler的invoke()方法获取该代理对象
         *                  并使用该代理对象执行原对象的方法
         */

        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, new InvocationHandler() {
            /**
             *
             * @param proxy 我们要代理的对象
             * @param method 我们要代理的对象的哪个方法
             * @param args 我们要代理的对象的方法的参数列表数组
             * @return  实现代理对象invoke()方法，通过代理对象执行原对象的方法的返回结果
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                ObjectInputStream input=null;
                ObjectOutputStream output=null;
                try {
                    /**
                     * 实现代理对象invoke()方法获取远程代理对象
                     * 并使用该代理对象调用原对象方法之前
                     * 我们需要通过socket发送 ,创建客户端socket
                     */
                    Socket socket=new Socket();
                    //建立连接
                    socket.connect(socketAddress);
                    /**
                     * 通过socket连接发送数据
                     * OutputStream 字节流
                     * ObjectOutputStream 对象流(序列化流)
                     */
                    output=new ObjectOutputStream(socket.getOutputStream());
                    /**
                     *
                     * 获取远程方动态代理对象，我们需要通过socket发送：
                     * 接口名，方法名，参数类型，参数名
                     */
                    output.writeUTF(serviceInterface.getName());
                    output.writeUTF(method.getName());
                    output.writeObject(method.getParameterTypes());
                    output.writeObject(args);
                    // 发送完以后等待服务端处理...
                    // 服务端完成处理，客户端接收处理结果
                    input=new ObjectInputStream(socket.getInputStream());
                    Object result = input.readObject();
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    try {
                        if (output!=null){
                            output.close();
                        }
                        if (input!=null){
                            input.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

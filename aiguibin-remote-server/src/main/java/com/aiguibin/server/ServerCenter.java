package com.aiguibin.server;

public interface ServerCenter {
    void  start();
    void  stop();
    void  register(Class service,Class serviceImpl);
}

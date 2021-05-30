package com.aiguibin.service;

public class HelloWorldServiceImpl implements HelloWorldService{
    @Override
    public String sayHelloName(String name) {

        return String.format("Hello"+name);
    }
}

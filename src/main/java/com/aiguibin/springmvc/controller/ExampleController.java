package com.aiguibin.springmvc.controller;

import com.aiguibin.springmvc.annotation.AIguibinController;
import com.aiguibin.springmvc.annotation.AIguibinRequestMapping;

@AIguibinController
@AIguibinRequestMapping(value = "/")
public class ExampleController {

    @AIguibinRequestMapping(value = "example")
    public String example(String name){

        return name;
    }
}

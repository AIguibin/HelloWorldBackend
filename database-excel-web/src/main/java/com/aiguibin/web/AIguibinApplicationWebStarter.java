package com.aiguibin.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class AIguibinApplicationWebStarter {

    public static void main(String[] args) {
        SpringApplication.run(AIguibinApplicationWebStarter.class, args);
    }
}

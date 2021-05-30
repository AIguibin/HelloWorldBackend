package com.aiguibin.entities;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class UserInfoEntity {
    private Long id;
    private Integer age;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 家庭地址
     */
    private Integer address;
    /**
     * 注册日期
     */
    private Long register;
    /**
     * 生日
     */
    private Date birthday;
    private Timestamp updateTime;
}

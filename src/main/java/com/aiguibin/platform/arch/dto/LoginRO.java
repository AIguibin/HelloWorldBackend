package com.aiguibin.platform.arch.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRO {
    @NotBlank(message = "账号不能为空")
    private String userNum;
    @NotBlank(message = "密码不能为空")
    private String password;
}
package com.aiguibin.platform.arch.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserCreateRequest {
    @NotBlank(message = "用户编号不能为空")
    private String usernumb;
    @NotBlank(message = "用户姓名不能为空")
    private String username;
    // 初始密码固定为 666666，不从入参接收
}
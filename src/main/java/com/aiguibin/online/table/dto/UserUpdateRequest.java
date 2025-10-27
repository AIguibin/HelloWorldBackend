package com.aiguibin.online.table.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserUpdateRequest {
    @NotNull(message = "ID不能为空")
    private Long id;
    @NotBlank(message = "用户编号不能为空")
    private String usernumb;
    @NotBlank(message = "用户姓名不能为空")
    private String username;
    private String password; // 可选，更新时如为空则不改
}
package com.aiguibin.domain;

import lombok.Data;

@Data
public class UserDTO {
    private String name;
    private String password;
    private String confirm;
}

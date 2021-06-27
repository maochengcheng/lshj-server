package com.cq1080.auth.bean;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PasswordForm {
    @NotEmpty(message = "密码不能为空")
    private String password;
}

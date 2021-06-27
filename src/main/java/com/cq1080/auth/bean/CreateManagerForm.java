package com.cq1080.auth.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateManagerForm {

    private Integer id;

    @ApiModelProperty(name = "登录账号")
    private String mobile;

    @ApiModelProperty(name = "账号名称")
    private String name;

    @ApiModelProperty(name = "登录密码")
    private String password;

    @ApiModelProperty(name = "单位名称")
    private String detentionName;

    @ApiModelProperty(name = "用户角色")
    private List<Role> roles;

}

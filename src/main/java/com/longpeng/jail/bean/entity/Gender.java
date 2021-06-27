package com.longpeng.jail.bean.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Gender",description = "性别")
public enum Gender {
    /**
     * 男
     */
    @ApiModelProperty(value = "男")
    MALE,
    /**
     * 女
     */
    @ApiModelProperty(value = "女")
    FEMALE
}

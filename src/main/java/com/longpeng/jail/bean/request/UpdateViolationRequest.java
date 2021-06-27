package com.longpeng.jail.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UpdateViolationRequest",description = "编辑律师系统违约次数接收参数")
public class UpdateViolationRequest {

    @ApiModelProperty(value = "人工违约次数")
    private Integer artificialViolation;

    @ApiModelProperty(value = "律师id")
    private Integer attorneyId;

}

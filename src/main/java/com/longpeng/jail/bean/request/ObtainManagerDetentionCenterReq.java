package com.longpeng.jail.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ObtainManagerDetentionCenterReq",description = "获取管理员管理单位请求参数")
public class ObtainManagerDetentionCenterReq {

    @ApiModelProperty(value = "管理人id")
    private Integer managerId;
}

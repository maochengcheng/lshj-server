package com.longpeng.jail.bean.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AttorneyViolationListResponse",description = "获取违约详细列表返回")
public class AttorneyViolationListResponse {

    @ApiModelProperty(value = "律师id")
    private Integer attorneyId;

    @ApiModelProperty(value = "律师姓名")
    private String attorneyName;

    @ApiModelProperty(value = "律师证件号")
    private String licenseNumber;

    @ApiModelProperty(value = "身份证号")
    private String idNumber;

    @ApiModelProperty(value = "人工设置违约数")
    private Integer artificialViolation;

    @ApiModelProperty(value = "系统违约次数")
    private Integer violationNumber;

}

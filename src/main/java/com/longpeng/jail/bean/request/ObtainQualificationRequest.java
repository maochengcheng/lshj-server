package com.longpeng.jail.bean.request;

import com.longpeng.jail.bean.entity.Certification;
import com.longpeng.jail.util.pageHelper.PageRequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ObtainQualificationRequest",description = "获取律师审核资料接收参数")
@Data
public class ObtainQualificationRequest extends PageRequestBase {

    @ApiModelProperty(value = "开始时间yyyy-HH-mm 00:00:00")
    private String start;

    @ApiModelProperty(value = "开始时间yyyy-HH-mm 23:59:59")
    private String end;

    @ApiModelProperty(value = "律师姓名")
    private String name;

    @ApiModelProperty(value = "审核状态")
    private Certification certification;



}

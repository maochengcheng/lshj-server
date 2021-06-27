package com.longpeng.jail.bean.request;

import com.longpeng.jail.util.pageHelper.PageRequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ObtainAccessViolationRequest",description = "获取律师违约预约列表")
public class ObtainAccessViolationRequest extends PageRequestBase {
    @ApiModelProperty(value = "律师id")
    private Integer attorneyId;
}

package com.longpeng.jail.bean.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DashboardResponse",description = "首页看板返回")
public class DashboardResponse {

    @ApiModelProperty(value = "律师库人数统计")
    private Integer attorneyNumber;

    @ApiModelProperty(value = "预约总数统计")
    private Integer accessTotalNumber;

    @ApiModelProperty(value = "当日预约数")
    private Integer accessTodayTotalNumber;

    @ApiModelProperty(value = "当日会见数")
    private Integer accessMeetTodayNumber;


}

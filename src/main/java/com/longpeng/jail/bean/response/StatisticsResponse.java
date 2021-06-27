package com.longpeng.jail.bean.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "StatisticsResponse",description = "数据统计返回")
public class StatisticsResponse {

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "数量")
    private Integer number;
}

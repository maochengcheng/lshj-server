package com.longpeng.jail.bean.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "WorkingDayResponse",description = "")
public class WorkingDayResponse {
    private String date;
    private boolean isWork=false;
}

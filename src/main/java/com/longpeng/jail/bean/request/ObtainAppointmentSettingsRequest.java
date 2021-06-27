package com.longpeng.jail.bean.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(value = "ObtainAppointmentSettingsRequest",description = "获取预约设置接收参数接收参数")
@Data
public class ObtainAppointmentSettingsRequest {

    private Integer type;
}

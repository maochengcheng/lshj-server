package com.longpeng.jail.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ExportViolationLogExcelReq",description = "导出操作日志接收参数")
public class ExportViolationLogExcelReq {

    @ApiModelProperty(value = "开始时间yyyy-HH-mm 00:00:00")
    private String start;

    @ApiModelProperty(value = "开始时间yyyy-HH-mm 23:59:59")
    private String end;

    @ApiModelProperty(value = "管理员姓名")
    private String managerName;

    @ApiModelProperty(value = "律师姓名")
    private String attorneyName;

}

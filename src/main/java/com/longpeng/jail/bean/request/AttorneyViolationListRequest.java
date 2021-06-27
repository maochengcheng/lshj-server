package com.longpeng.jail.bean.request;

import com.longpeng.jail.util.pageHelper.PageRequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AttorneyViolationListRequest",description = "获取违约详细列表接收参数")
public class AttorneyViolationListRequest extends PageRequestBase {

    @ApiModelProperty(value = "律师姓名")
    private String attorneyName;

}

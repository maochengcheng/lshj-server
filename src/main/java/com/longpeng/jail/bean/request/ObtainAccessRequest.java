package com.longpeng.jail.bean.request;

import com.longpeng.jail.bean.entity.Access;
import com.longpeng.jail.bean.entity.Certification;
import com.longpeng.jail.util.pageHelper.PageRequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ObtainAccessRequest",description = "小程序获取预约信息")
public class ObtainAccessRequest extends PageRequestBase {

    @ApiModelProperty(value = "预约类型 1-预约会见 2-远程会见")
    private Access.Type type;

    @ApiModelProperty(value = "待审核 未通过 通过 取消")
    private Certification certification;

}

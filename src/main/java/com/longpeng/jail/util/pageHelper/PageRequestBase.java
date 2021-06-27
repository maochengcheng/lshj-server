package com.longpeng.jail.util.pageHelper;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageRequestBase {
    @ApiModelProperty(value = "每页条数")
    private Integer pageSize=10;

    @ApiModelProperty(value = "当前页")
    private Integer pageNum=1;
}

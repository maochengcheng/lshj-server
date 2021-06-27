package com.longpeng.jail.bean.request;

import com.longpeng.jail.bean.entity.Access;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "AccessBatchRequest",description = "批量操作接收参数")
public class AccessBatchRequest {

    private Integer accessId;

    private Access.AccessStatus accessStatus;

}

package com.longpeng.jail.bean.request;

import com.longpeng.jail.bean.entity.Certification;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "InsertQualificationRequest",description = "审核律师资料接收参数")
public class InsertQualificationRequest {

    private Certification certification;

    private Integer attorneyId;
}

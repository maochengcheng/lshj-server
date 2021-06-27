package com.longpeng.jail.bean.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.longpeng.jail.bean.entity.Access;
import com.longpeng.jail.bean.entity.Certification;
import com.longpeng.jail.bean.entity.Qualification;
import com.longpeng.jail.util.TimestampDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel(value = "InsertAccessRequest",description = "申请预约接收参数")
public class InsertAccessRequest {

    @ApiModelProperty(value = "单位id")
    private Integer detentionCenterId;

    @ApiModelProperty(value = "单位名称")
    private String detentionCenterName;


//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
//    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp appointmentTime;

    @ApiModelProperty(value = "预约时间")
    private long appointmentTimeLong;

    @ApiModelProperty(value = "车牌号")
    private String numberplate;

    @ApiModelProperty(value = "订餐数量")
    private Integer orderQuantity;

    @ApiModelProperty(value = "预约审核状态 未审核 未通过 已取消 已通过")
    private Certification certification;

    @ApiModelProperty(value = "嫌疑人名称")
    private String suspectsName;

    @ApiModelProperty(value = "出生日期")
    private String birthDay;

    @ApiModelProperty(value = "是否第一次会面")
    private boolean isFirst;

    @ApiModelProperty(value = "诉讼状态")
    private String lawsuitStatus;

    @ApiModelProperty(value = "预约类型 1-预约会见 2-远程会见")
    private Access.Type type;

    @ApiModelProperty(value = "介绍信和委托书")
    private List<Qualification> qualifications;

}

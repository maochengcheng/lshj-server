package com.longpeng.jail.bean.entity;

import com.cq1080.jpa.entity.BaseEntity;
import com.cq1080.meta.annotation.Meta;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@ApiModel(value = "AppointmentSettings",description = "预约系统设置")
public class AppointmentSettings extends BaseEntity {

    @ApiModelProperty(value = "1-时间段")
    private String period;

    @ApiModelProperty(value = "2-文字简介 6-电话")
    private String note;

    @ApiModelProperty(value = "预约设置类型 1-时间段设置 2-会见须知 3-时间段预约人数设置 4-温馨提示 5-违约设置 6-联系人电话 7-远程预约席位设置 8-预约访问规则 9-预约会见温馨提示 10-注册首页提示 11-注册详情页")
    private Integer type;

    @ApiModelProperty(value = "1-时间段排序 3-日期周 5-违约次数 6-排序 8-（1-律师一天只能预约一次 2-律师上午预约一次下午预约一次）")
    private Integer serialNumber;

    @ApiModelProperty(value = "3-预约人数设置 5-违约封禁时间（单位天） 7-远程预约人数")
    private Integer peopleNumber;

    @ApiModelProperty(name = "单位名称")
    private String detentionName;
}

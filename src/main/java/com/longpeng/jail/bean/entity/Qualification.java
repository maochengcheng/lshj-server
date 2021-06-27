package com.longpeng.jail.bean.entity;

import com.cq1080.jpa.entity.BaseEntity;
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
@ApiModel(value = "Qualification",description = "审核资料表")
public class Qualification extends BaseEntity {

    @ApiModelProperty(value = "律师id")
    private Integer attorneyId;

    @ApiModelProperty(value = "预约id")
    private Integer accessId;

    @ApiModelProperty(value = "文件路径")
    private String path;

    @ApiModelProperty(value = "资料类型 1-律师审核资料 2-犯罪嫌疑人、被告人专用介绍信 3-当事人委托书 4-预约律师资料")
    private Integer type;

}

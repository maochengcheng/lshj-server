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
@ApiModel(value = "ManagerDetentionCenter",description = "单位管理员")
public class ManagerDetentionCenter  extends BaseEntity {

    @ApiModelProperty(value = "管理人id")
    private Integer managerId;

    @ApiModelProperty(value = "单位id")
    private Integer detentionCenterId;

    @ApiModelProperty(value = "单位名称")
    private String detentionCenterName;

}

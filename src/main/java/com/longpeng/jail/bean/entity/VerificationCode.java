package com.longpeng.jail.bean.entity;

import com.cq1080.jpa.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@Data
@ApiModel(value = "VerificationCode",description = "验证码")
public class VerificationCode extends BaseEntity {

    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(value = "手机号")
    private String phone;
}

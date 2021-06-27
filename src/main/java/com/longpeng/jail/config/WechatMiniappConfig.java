package com.longpeng.jail.config;

import com.cq1080.jpa.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@ApiModel(value = "WechatMiniappConfig",description = "微信小程序配置")
public class WechatMiniappConfig extends BaseEntity {

    @ApiModelProperty(value = "小程序 appId")
    private String appId;

    @ApiModelProperty(value = "小程序 appSecret")
    private String secret;

}

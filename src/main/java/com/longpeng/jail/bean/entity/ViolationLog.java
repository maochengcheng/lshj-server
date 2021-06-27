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

@Entity
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@Data
@ApiModel(value = "ViolationLog",description = "编辑违规操作日志")
public class ViolationLog extends BaseEntity {

    @ApiModelProperty(value = "管理员id")
    private Integer managerId;

    @ApiModelProperty(value = "管理员名称")
    @Meta(displayInList = true,searchable = true)
    private String managerName;

    @ApiModelProperty(value = "律师id")
    private Integer attorneyId;

    @ApiModelProperty(value = "律师名称")
    @Meta(displayInList = true,searchable = true)
    private String attorneyName;

    @ApiModelProperty(value = "操作内容")
    @Meta(displayInList = true)
    private String note;
}

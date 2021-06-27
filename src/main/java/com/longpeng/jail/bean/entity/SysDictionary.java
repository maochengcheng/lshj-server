package com.longpeng.jail.bean.entity;

import com.cq1080.jpa.entity.BaseEntity;
import com.cq1080.meta.annotation.Meta;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Transient;
import java.util.List;


@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@ApiModel(value = "SysDictionary",description = "数据字典表")
@Meta()
public class SysDictionary extends BaseEntity {

    @ApiModelProperty(value = "字典编码")
    @Meta(displayInList = true,searchable = true)
    private String dicCode;

    @ApiModelProperty(value = "字典名称")
    @Meta(displayInList = true,searchable = true)
    private String dicName;

    @ApiModelProperty(value = "字典项编码")
    private String itemCode;

    @ApiModelProperty(value = "字典项名称")
    private String itemName;

    @ApiModelProperty(value = "层级")
    @Meta(searchable = true)
    private String status;

}

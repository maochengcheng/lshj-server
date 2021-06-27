package com.longpeng.jail.bean.entity;

import com.cq1080.jpa.entity.BaseEntity;
import com.cq1080.meta.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@ApiModel(value = "Attorney",description = "律师库")
@Meta(insertable = false)
public class Attorney extends BaseEntity {


    @ApiModelProperty(value = "微信头像")
    @Meta(displayInList = true,type = ContentType.IMAGE)
    private String wechatAvatar;

    @ApiModelProperty(value = "本人头像")
//    @Meta(displayInList = true,type = ContentType.IMAGE)
    private String faceImg;

    @ApiModelProperty(value = "姓名")
    @Meta(displayInList = true,searchable = true)
    private String name;

    @ApiModelProperty(value = "性别",notes = "男 女")
    @Enumerated(EnumType.STRING)
    @Meta(displayInList = true,searchable = true)
    private Gender gender;

    @ApiModelProperty(value = "手机号")
    @Meta(displayInList = true,searchable = true)
    private String phone;

    @ApiModelProperty(value = "身份证号")
    @Meta(displayInList = true,searchable = true)
    private String idNumber;

    @ApiModelProperty(value = "律师证件号")
    @Meta(displayInList = true,searchable = true)
    private String licenseNumber;

    @ApiModelProperty(value = "律师事务所")
    @Meta(displayInList = true,searchable = true)
    private String lawOffice;

    @ApiModelProperty(value = "人工编辑的违约数")
    @Column(insertable = false, columnDefinition = "int default 0")
    private Integer artificialViolation;

    @ApiModelProperty(value = "微信登录用户唯一openId")
    private String openId;

    @ApiModelProperty(value = "身份认证状态",notes = " 未审核 未通过 通过")
//    @Column(columnDefinition = "varchar default UNREVIEWED")
    @Enumerated(EnumType.STRING)
    private Certification certification;

    @Transient
    private String token;

    @Transient
    @ApiModelProperty(value = "审核资料")
    private List<Qualification> qualifications;

    @Transient
    @ApiModelProperty(value = "违约次数")
    @Meta(displayInList = true)
    private Integer violationNumber;

    @Transient
    @ApiModelProperty(value = "解封日期")
    private String violationDate;


}

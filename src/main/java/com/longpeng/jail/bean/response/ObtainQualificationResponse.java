package com.longpeng.jail.bean.response;



import com.longpeng.jail.bean.entity.Certification;
import com.longpeng.jail.bean.entity.Gender;
import com.longpeng.jail.bean.entity.Qualification;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "ObtainQualificationResponse",description = "审核资料")
public class ObtainQualificationResponse {

    @ApiModelProperty(value = "律师id")
    private Integer attorneyId;

    @ApiModelProperty(value = "本人头像")
    private String faceImg;

    @ApiModelProperty(value = "微信头像")
    private String wechatAvatar;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "性别 男 女")
    private Gender gender;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "身份证号")
    private String idNumber;

    @ApiModelProperty(value = "律师证件号")
    private String licenseNumber;

    @ApiModelProperty(value = "律师事务所")
    private String lawOffice;

    @ApiModelProperty(value = "身份认证状态 未审核 未通过 通过")
    private Certification certification;

    @ApiModelProperty(value = "资料图片列表")
    private List<Qualification> qualifications;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

}

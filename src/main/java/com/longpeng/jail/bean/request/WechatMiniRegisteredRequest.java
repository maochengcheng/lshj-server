package com.longpeng.jail.bean.request;


import com.longpeng.jail.bean.entity.Gender;
import com.longpeng.jail.bean.entity.Qualification;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "WechatMiniRegisteredRequest",description = "微信小程序注册")
@Data
public class WechatMiniRegisteredRequest {
    @ApiModelProperty(value = "微信头像")
    private String wechatAvatar;

    @ApiModelProperty(value = "本人头像")
    private String faceImg;

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

    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(value = "律师执业证")
    private List<Qualification> qualifications;
}

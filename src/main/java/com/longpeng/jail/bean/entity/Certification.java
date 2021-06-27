package com.longpeng.jail.bean.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value = "Certification",description = "审核状态")
public enum Certification {
    /**
     * 待审核
     */
    @ApiModelProperty(value = "待审核")
    UNREVIEWED,
    /**
     * 未通过
     */
    @ApiModelProperty(value = "未通过")
    REJECTED,
    /**
     * 通过
     */
    @ApiModelProperty(value = "通过")
    PASS,
    /**
     * 取消
     */
    @ApiModelProperty(value = "取消")
    CANCEL;

    public static String enumToString(Certification certification){
        String str="其他";
        switch (certification) {
            case UNREVIEWED:
                str="待审核";
                break;
            case REJECTED:
                str="未通过";
                break;
            case PASS:
                str="通过";
                break;
            case CANCEL:
                str="取消";
                break;
        }
        return str;
    }
}

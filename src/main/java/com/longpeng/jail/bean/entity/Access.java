package com.longpeng.jail.bean.entity;

import com.cq1080.jpa.entity.BaseEntity;
import com.cq1080.meta.annotation.ContentType;
import com.cq1080.meta.annotation.Meta;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@ApiModel(value = "Access",description = "预约信息")
@Meta()
public class Access extends BaseEntity {

    @ApiModelProperty(value = "预约人id")
    private Integer attorneyId;

    @ApiModelProperty(value = "预约人姓名")
    @Meta(displayInList = true,searchable = true)
    private String attorneyName;

    @ApiModelProperty(value = "单位id")
    private Integer detentionCenterId;

    @ApiModelProperty(value = "订餐数量")
    @Meta(displayInList = false)
    private Integer orderQuantity;

    @ApiModelProperty(value = "单位名称")
    @Meta(displayInList = true,searchable = true)
    private String detentionCenterName;

    @ApiModelProperty(value = "预约时间")
    @Meta(displayInList = true,searchable = true,type = ContentType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Timestamp appointmentTime;

    @ApiModelProperty(value = "到访时间")
//    @Meta(displayInList = true,searchable = true,type = ContentType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Timestamp visitTime;

    @ApiModelProperty(value = "车牌号")
    @Meta(displayInList = false,searchable = false)
    private String numberplate;

    @ApiModelProperty(value = "预约审核状态",notes = "未审核 未通过 已取消 已通过")
//    @Column(columnDefinition = "varchar default UNREVIEWED")
    @Enumerated(EnumType.STRING)
    @Meta(displayInList = true,searchable = true)
    private Certification certification;

    @ApiModelProperty(value = "预约状态",notes = "未到访 已到访 会面结束 爽约")
//    @Column(columnDefinition = "varchar default NOTVISITED")
    @Enumerated(EnumType.STRING)
    @Meta(displayInList = true,searchable = true)
    private AccessStatus accessStatus;

    @ApiModelProperty(value = "嫌疑人名称")
    @Meta(displayInList = true,searchable = true)
    private String suspectsName;

    @Meta(displayInList = true)
    @ApiModelProperty(value = "出生日期")
    private String birthDay;

    @ApiModelProperty(value = "是否第一次会面")
    @Meta(displayInList = true,searchable = true,options = {"true:是","false:否"})
    private Boolean isFirst;

    @ApiModelProperty(value = "预约类型",notes = "预约会见 远程会见")
    @Meta(displayInList = false,searchable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @ApiModelProperty(value = "备注")
    @Meta(displayInList = true)
    private String note;

    @ApiModelProperty(value = "诉讼状态")
    @Meta(displayInList = true)
    private String lawsuitStatus;

    @Transient
    @ApiModelProperty(value = "介绍信")
    private List<Qualification> qualifications1;

    @Transient
    @ApiModelProperty(value = "委托书")
    private List<Qualification> qualifications2;

    @Transient
    @ApiModelProperty(value = "预约律师资料")
    private List<Qualification> qualifications3;

    public enum AccessStatus{
        /**
         * 未到访
         */
        @ApiModelProperty(value = "未到访")
        NOTVISITED,
        /**
         * 已到访
         */
        @ApiModelProperty(value = "已到访")
        VISITED,
        /**
         * 会面结束
         */
        @ApiModelProperty(value = "会面结束")
        END,
        /**
         * 违约
         */
        @ApiModelProperty(value = "违约")
        VIOLATION

    }

    public enum Type{
        /**
         * 预约会见
         */
        @ApiModelProperty(value = "预约会见")
        OFFLINE,
        /**
         * 远程会见
         */
        @ApiModelProperty(value = "远程会见")
        ONLINE
    }
}

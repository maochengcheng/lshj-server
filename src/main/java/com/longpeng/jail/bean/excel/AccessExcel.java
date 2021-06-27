package com.longpeng.jail.bean.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.longpeng.jail.bean.entity.Access;
import com.longpeng.jail.bean.entity.Certification;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class AccessExcel {

    @ExcelProperty("序号")
    private Integer number;

    @ExcelProperty("嫌疑人姓名")
    private String suspectsName;

    @ExcelProperty("律师姓名")
    private String attorneyName;

    @ExcelProperty("身份证号")
    @ColumnWidth(25)
    private String idNumber;

    @ExcelProperty("律师证件号")
    @ColumnWidth(25)
    private String licenseNumber;

    @ExcelProperty("律师事务所")
    @ColumnWidth(25)
    private String lawOffice;

    @ExcelProperty("律师电话")
    @ColumnWidth(25)
    private String phone;

    @ExcelProperty("会见日期")
    @ColumnWidth(25)
    private String date;

    @ExcelProperty("会见时间段")
    @ColumnWidth(25)
    private String period;

    @ExcelProperty("会见地点")
    @ColumnWidth(30)
    private String detentionCenterName;

    @ExcelProperty("是否第一次会面")
    private String isFirstExcel;

    @ExcelProperty("车牌号")
    @ColumnWidth(20)
    private String numberplate;

//    @ExcelProperty("预约时间")
//    @ColumnWidth(30)
    @ExcelIgnore
    private Date appointmentTimeExcel;

//    @ExcelProperty("到访时间")
//    @ColumnWidth(30)
    @ExcelIgnore
    private Date visitTimeExcel;


    @ExcelProperty("预约类型")
//    @ExcelIgnore
    private String typeExcel;

    @ExcelProperty("预约审核状态")
//    @ExcelIgnore
    private String certificationExcel;

//    @ExcelProperty("预约状态")
    @ExcelIgnore
    private String accessStatusExcel;

    @ExcelIgnore
    private Timestamp appointmentTime;

    @ExcelIgnore
    private Timestamp visitTime;

    @ExcelIgnore
    private Boolean isFirst;

    @ExcelIgnore
    private Certification certification;

    @ExcelIgnore
    private Access.AccessStatus accessStatus;

    @ExcelIgnore
    private Access.Type type;

    @ExcelIgnore
    private Integer attorneyId;
}

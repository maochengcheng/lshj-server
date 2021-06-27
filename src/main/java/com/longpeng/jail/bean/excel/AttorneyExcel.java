package com.longpeng.jail.bean.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class AttorneyExcel {

    @ExcelIgnore
    private Integer id;

    @ExcelProperty("律师姓名")
    private String name;

    @ExcelIgnore
    private String gender;

    @ExcelProperty("性别")
    private String sex;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("身份证号")
    private String idNumber;

    @ExcelProperty("律师证件号")
    @ColumnWidth(20)
    private String licenseNumber;

    @ExcelProperty("预约总次数")
    private Integer accessNumber;

    @ExcelProperty("今年违约次数")
    private Integer violationNumber;


    @ExcelIgnore
    private Timestamp createTime;

    @ExcelIgnore
    private Integer artificialViolation;

    @ExcelProperty("注册时间")
    @ColumnWidth(20)
    private Date date;


}

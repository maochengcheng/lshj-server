package com.longpeng.jail.bean.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class ViolationLogExcel {

    @ExcelProperty("管理员名称")
    private String managerName;

    @ExcelProperty("律师名称")
    private String attorneyName;

    @ExcelProperty("操作时间")
    private Date operatingTime;

    @ExcelProperty("操作内容")
    private String note;

    @ExcelIgnore
    private Timestamp createTime;
}

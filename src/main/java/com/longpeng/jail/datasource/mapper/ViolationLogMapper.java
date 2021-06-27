package com.longpeng.jail.datasource.mapper;

import com.longpeng.jail.bean.excel.ViolationLogExcel;
import com.longpeng.jail.bean.request.ExportViolationLogExcelReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@Mapper
public interface ViolationLogMapper {

    @SelectProvider(type = violationLogSqlProvide.class,method = "exportViolationLogExcel")
    List<ViolationLogExcel> exportExcel(ExportViolationLogExcelReq exportViolationLogExcelReq);

    class violationLogSqlProvide{
        public String exportViolationLogExcel(ExportViolationLogExcelReq exportViolationLogExcelReq){
            return new SQL(){{
                SELECT("*");
                FROM("violation_log");
                if(!StringUtils.isEmpty(exportViolationLogExcelReq.getStart()) && !StringUtils.isEmpty(exportViolationLogExcelReq.getEnd())){
                    WHERE("create_time>=#{start} and create_time<=#{end}");
                }
                if(!StringUtils.isEmpty(exportViolationLogExcelReq.getManagerName())){
                    WHERE("manager_name like concat('%',#{managerName},'%')");
                }
                if(!StringUtils.isEmpty(exportViolationLogExcelReq.getAttorneyName())){
                    WHERE("manager_name like concat('%',#{managerName},'%')");
                }
            }}.toString();
        }

    }
}

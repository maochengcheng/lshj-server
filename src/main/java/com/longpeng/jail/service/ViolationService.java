package com.longpeng.jail.service;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.cq1080.bean.form.PageForm;
import com.cq1080.jpa.specification.SpecificationUtil;
import com.cq1080.rest.APIError;
import com.github.pagehelper.PageHelper;
import com.github.wenhao.jpa.Specifications;
import com.longpeng.jail.bean.entity.Access;
import com.longpeng.jail.bean.entity.Attorney;
import com.longpeng.jail.bean.entity.ViolationLog;
import com.longpeng.jail.bean.excel.ViolationLogExcel;
import com.longpeng.jail.bean.request.AttorneyViolationListRequest;
import com.longpeng.jail.bean.request.ExportViolationLogExcelReq;
import com.longpeng.jail.bean.request.ObtainAccessViolationRequest;
import com.longpeng.jail.bean.request.UpdateViolationRequest;
import com.longpeng.jail.bean.response.AttorneyViolationListResponse;
import com.longpeng.jail.config.ProjectConfig;
import com.longpeng.jail.datasource.mapper.AccessMapper;
import com.longpeng.jail.datasource.mapper.AttorneyMapper;
import com.longpeng.jail.datasource.mapper.ViolationLogMapper;
import com.longpeng.jail.datasource.repository.AccessRepository;
import com.longpeng.jail.datasource.repository.AttorneyRepository;
import com.longpeng.jail.datasource.repository.ViolationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@Service
public class ViolationService extends BaseService{

    private AttorneyMapper attorneyMapper;
    private AccessMapper accessMapper;
    private AttorneyRepository attorneyRepository;
    private AccessRepository accessRepository;
    private ViolationLogRepository violationLogRepository;
    private ProjectConfig projectConfig;
    private ViolationLogMapper violationLogMapper;


    @Autowired
    public ViolationService(AttorneyMapper attorneyMapper, AccessMapper accessMapper, AttorneyRepository attorneyRepository, AccessRepository accessRepository, ViolationLogRepository violationLogRepository, ProjectConfig projectConfig, ViolationLogMapper violationLogMapper) {
        this.attorneyMapper = attorneyMapper;
        this.accessMapper = accessMapper;
        this.attorneyRepository = attorneyRepository;
        this.accessRepository = accessRepository;
        this.violationLogRepository = violationLogRepository;
        this.projectConfig = projectConfig;
        this.violationLogMapper = violationLogMapper;
    }

    /**
     * 获取违约列表
     * @param attorneyViolationListRequest
     * @return
     */
    public List<AttorneyViolationListResponse> attorneyViolationListResponses(AttorneyViolationListRequest attorneyViolationListRequest){
        PageHelper.startPage(attorneyViolationListRequest.getPageNum(),attorneyViolationListRequest.getPageSize());
        List<AttorneyViolationListResponse> attorneyViolationListResponses = attorneyMapper.getAttorneyViolationListResponses(attorneyViolationListRequest);
        for(int i=0;i<attorneyViolationListResponses.size();i++){
            Integer violationNumberByAid = accessMapper.getViolationNumberByAid(attorneyViolationListResponses.get(i).getAttorneyId());
            attorneyViolationListResponses.get(i).setViolationNumber(violationNumberByAid);
        }
        return attorneyViolationListResponses;
    }

    /**
     * 更新人工违约次数
     * updateViolation
     * @param updateViolationRequest
     */
    public void updateViolation(UpdateViolationRequest updateViolationRequest){
        Attorney attorney = attorneyRepository.findOne(Specifications.<Attorney>and().eq("id", updateViolationRequest.getAttorneyId())
                .eq("presenceStatus", 1).build()).orElse(null);
        if(attorney==null){
            APIError.e(400,"律师已经不存在");
        }
        Timestamp appointmentTime = accessMapper.getAppointmentTime(attorney.getId());
        if(appointmentTime==null){
            APIError.e(400,"律师没有过预约记录，不能设置人工违约数");
        }
        ViolationLog violationLog=new ViolationLog();
        violationLog.setAttorneyId(attorney.getId());
        violationLog.setAttorneyName(attorney.getName());
        violationLog.setManagerId(getManager().getId());
        violationLog.setManagerName(getManager().getName());
        violationLog.setNote("将人工违约次数从"+attorney.getArtificialViolation()+"次，修改为"+updateViolationRequest.getArtificialViolation()+"次");
        violationLogRepository.save(violationLog);
        attorney.setArtificialViolation(updateViolationRequest.getArtificialViolation());
        attorneyRepository.save(attorney);

    }

    /**
     * 获取律师违约预约列表
     * @param obtainAccessViolationRequest
     * @return
     */
    public List<Access> obtainAccessViolation(ObtainAccessViolationRequest obtainAccessViolationRequest){
        PageHelper.startPage(obtainAccessViolationRequest.getPageNum(),obtainAccessViolationRequest.getPageSize());
        List<Access> accesses = accessMapper.obtainAccessViolation(obtainAccessViolationRequest.getAttorneyId());
        return accesses;
    }

    /**
     * 消除系统违约记录
     * @param accessId
     */
    public void updateAccessViolation(Integer accessId){
        Access access = accessRepository.findOne(Specifications.<Access>and().eq("id", accessId)
                .eq("presenceStatus", 1).build()).orElse(null);
        if(access==null){
            APIError.e(400,"律师已经不存在");
        }
        access.setAccessStatus(Access.AccessStatus.END);
        accessRepository.save(access);
        ViolationLog violationLog=new ViolationLog();
        violationLog.setAttorneyId(access.getId());
        violationLog.setAttorneyName(access.getAttorneyName());
        violationLog.setManagerId(getManager().getId());
        violationLog.setManagerName(getManager().getName());
        violationLog.setNote("消除一条预约违约记录");
        violationLogRepository.save(violationLog);
    }

    /**
     * 获取操作日志
     * @param violationLog
     * @param pageForm
     * @return
     */
    public Page<ViolationLog> getViolationLog(ViolationLog violationLog, PageForm pageForm){
        Page<ViolationLog> violations = violationLogRepository.findAll(SpecificationUtil.<ViolationLog>filter(violationLog, pageForm).build(), pageForm.pageRequest());
        return violations;
    }

    /**
     * 导出操作日志excel
     * @param exportViolationLogExcelReq
     * @return
     */
    public String exportViolationLogExcel(ExportViolationLogExcelReq exportViolationLogExcelReq){
        String name= "操作日志"+ System.currentTimeMillis()+ ".xlsx";
        String excelUrl= projectConfig.getUploadPath() + File.separator + "excel" + File.separator + name;
        ExcelWriter excelWriter = EasyExcel.write(excelUrl, ViolationLogExcel.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
        List<ViolationLogExcel> violationLogExcels = violationLogMapper.exportExcel(exportViolationLogExcelReq);
        for(int i=0;i<violationLogExcels.size();i++){
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(violationLogExcels.get(i).getCreateTime().getTime());
            violationLogExcels.get(i).setOperatingTime(calendar.getTime());
        }
        excelWriter.write(violationLogExcels, writeSheet);
        excelWriter.finish();
        return "excel/"+name;
    }



}

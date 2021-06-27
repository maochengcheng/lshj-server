package com.longpeng.jail.service;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeData;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.cq1080.auth.bean.Manager;
import com.cq1080.auth.service.AuthManagerService;
import com.cq1080.bean.form.PageForm;
import com.cq1080.jpa.specification.SpecificationUtil;
import com.cq1080.rest.APIError;
import com.cq1080.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.wenhao.jpa.Sorts;
import com.github.wenhao.jpa.Specifications;
import com.longpeng.jail.bean.entity.*;
import com.longpeng.jail.bean.excel.AccessExcel;
import com.longpeng.jail.bean.request.*;
import com.longpeng.jail.bean.response.WorkingDayResponse;
import com.longpeng.jail.config.ProjectConfig;
import com.longpeng.jail.datasource.mapper.AccessMapper;
import com.longpeng.jail.datasource.mapper.AppointmentSettingsMapper;
import com.longpeng.jail.datasource.repository.*;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AccessService extends BaseService{

    private AccessRepository accessRepository;
    private AccessMapper accessMapper;
    private AppointmentSettingsRepository appointmentSettingsRepository;
    private QualificationRepository qualificationRepository;
    private AppointmentSettingsMapper appointmentSettingsMapper;
    private ProjectConfig projectConfig;
    private WechatMiniappService wechatMiniappService;
    private ViolationLogRepository violationLogRepository;
    private ManagerDetentionCenterRepository managerDetentionCenterRepository;
    @Autowired
    private AuthManagerService authManagerService;

    @Autowired
    public AccessService(AccessRepository accessRepository, AccessMapper accessMapper, AppointmentSettingsRepository appointmentSettingsRepository, QualificationRepository qualificationRepository, AppointmentSettingsMapper appointmentSettingsMapper, ProjectConfig projectConfig, WechatMiniappService wechatMiniappService, ViolationLogRepository violationLogRepository, ManagerDetentionCenterRepository managerDetentionCenterRepository) {
        this.accessRepository = accessRepository;
        this.accessMapper = accessMapper;
        this.appointmentSettingsRepository = appointmentSettingsRepository;
        this.qualificationRepository = qualificationRepository;
        this.appointmentSettingsMapper = appointmentSettingsMapper;
        this.projectConfig = projectConfig;
        this.wechatMiniappService = wechatMiniappService;
        this.violationLogRepository = violationLogRepository;

        this.managerDetentionCenterRepository = managerDetentionCenterRepository;
    }

    /**
     * 获取预约信息列表
     * @param access
     * @param pageForm
     * @return
     */
    public Page<Access>  getAccesses(Access access, PageForm pageForm){
        if(!StringUtils.isEmpty(authManagerService.getCurrentManager().getDetentionName())){
            access.setDetentionCenterName(authManagerService.getCurrentManager().getDetentionName());
        }
        Page<Access> accesses = accessRepository.findAll(SpecificationUtil.<Access>filter(access, pageForm).build()
                , PageRequest.of(pageForm.getPage(),pageForm.getSize(), Sorts.builder().asc("createTime").build()));
        for(int i=0;i<accesses.getContent().size();i++){
            accesses.getContent().get(i).setQualifications1(qualificationRepository.findAll(Specifications.<Qualification>and().eq("type",2).eq("accessId",accesses.getContent().get(i).getId()).build()));
            accesses.getContent().get(i).setQualifications2(qualificationRepository.findAll(Specifications.<Qualification>and().eq("type",3).eq("accessId",accesses.getContent().get(i).getId()).build()));
            accesses.getContent().get(i).setQualifications3(qualificationRepository.findAll(Specifications.<Qualification>and().eq("type",4).eq("accessId",accesses.getContent().get(i).getId()).build()));
        }
        return accesses;
    }

    /**
     * 编辑预约备注
     * @param access
     */
    public void insertAccessNote(Access access){
        Access accessNote = accessRepository.findById(access.getId()).orElse(null);
        if(StringUtils.isEmpty(accessNote)){
            APIError.e(400,"编辑id不存在");
        }
        accessNote.setNote(access.getNote());
        accessRepository.save(accessNote);
    }

    /**
     * 编辑预约信息
     * @param access
     * @return
     */
    public Access insertAccess(Access access){
        Integer id=access.getId();
        accessRepository.save(access);
        Attorney attorney = attorneyRepository.findOne(Specifications.<Attorney>and().eq("id", access.getAttorneyId()).build()).orElse(null);
        if(attorney!=null && access.getAccessStatus()==Access.AccessStatus.NOTVISITED && (access.getCertification()==Certification.PASS || access.getCertification()==Certification.REJECTED)){
            String certifition=access.getCertification()==Certification.PASS ? "通过":"未通过";
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String appointmentTime=simpleDateFormat.format(new Date(access.getAppointmentTime().getTime()));
            WxMaSubscribeMessage wxMaSubscribeMessage = getWxMaSubscribeMessage(attorney.getOpenId(), access.getSuspectsName(), access.getDetentionCenterName(), appointmentTime, certifition);
            try {
                wechatMiniappService.sendSubscribeMsg(wxMaSubscribeMessage);
            } catch (WxErrorException e) {
                e.printStackTrace();
//                APIError.e(400,"审核通知发送失败");
            }
        }
        if(StringUtils.isEmpty(id)){
            List<Qualification> qualifications = new ArrayList<>();
            qualifications.addAll(access.getQualifications1());
            qualifications.addAll(access.getQualifications2());
            qualifications.addAll(access.getQualifications3());
            for(Qualification qualification:qualifications){
                qualification.setAccessId(access.getId());
                qualificationRepository.save(qualification);
            }
        }
        return access;
    }

    /**
     * 删除预约信息
     * @param ids
     */
    public void deleteAccesses(String ids){
        Integer[] integers = StringUtil.toIntArray(ids);
        for(Integer i:integers){
            Access access = accessRepository.findById(i).orElse(null);
            if(StringUtils.isEmpty(access)){
                continue;
            }
            ViolationLog violationLog=new ViolationLog();
            violationLog.setAttorneyId(access.getAttorneyId());
            violationLog.setAttorneyName(access.getAttorneyName());
            violationLog.setManagerId(getManager().getId());
            violationLog.setManagerName(getManager().getName());
            violationLog.setNote("将律师"+access.getAttorneyName()+"预约"+access.getSuspectsName()+"的预约记录删除了");
            violationLogRepository.save(violationLog);
            accessMapper.deleteAccessById(i);
        }
    }

    /**
     * 批量操作预约记录
     * @param accessBatchRequests
     */
    public void accessBatch(List<AccessBatchRequest> accessBatchRequests){
        for(AccessBatchRequest accessBatchRequest:accessBatchRequests){
            Access access = accessRepository.findOne(Specifications.<Access>and().eq("id", accessBatchRequest.getAccessId()).build()).orElse(null);
            if(access!=null){
                access.setAccessStatus(accessBatchRequest.getAccessStatus());
                accessRepository.save(access);
            }
        }
    }

    /**
     * 获取预约设置
     * @param type
     * @return
     */
    public List<AppointmentSettings> obatinAppointmentSettings(Integer type,String detentionName){
        List<AppointmentSettings> appointmentSettings;
        if(type.equals(6)){
            appointmentSettings = appointmentSettingsRepository.findAll(Specifications.<AppointmentSettings>and()
                    .eq("type", type)
                    .eq("detentionName", detentionName)
                    .build(),Sort.by(Sort.Order.asc("serialNumber")));
        }else if(type.equals(2) ||type.equals(4) ||type.equals(9) ||type.equals(10) ||type.equals(11)){
            if (StringUtils.isEmpty(detentionName)){
                detentionName=null;
            }
            appointmentSettings = appointmentSettingsRepository.findAll(Specifications.<AppointmentSettings>and()
                    .eq("type", type)
                    .eq("detentionName", detentionName)
                    .build(),Sort.by(Sort.Order.desc("updateTime")));
        }else {
            if (StringUtils.isEmpty(detentionName)){
                detentionName=null;
            }
            appointmentSettings = appointmentSettingsRepository.findAll(Specifications.<AppointmentSettings>and()
                    .eq("type", type)
                    .eq("detentionName", detentionName)
                    .build(), Sort.by(Sort.Order.asc("period")));
        }
        return appointmentSettings;
    }


    /**
     * 获取预约设置
     * @param type
     * @param timestamp
     * @return
     */
    public List<AppointmentSettings> wechatObatinAppointmentSettings(Integer type, Timestamp timestamp,String data,String detentionName){
        List<AppointmentSettings> appointmentSettings = appointmentSettingsRepository.findAll(Specifications.<AppointmentSettings>and()
                .eq("type", type)
                .eq("detentionName", detentionName)
                .build(),Sort.by(Sort.Order.asc("period")));
        Calendar cal = Calendar.getInstance();
        if(!StringUtils.isEmpty(timestamp)){
            cal.setTimeInMillis(timestamp.getTime());
        }
        if(type.equals(2) ||type.equals(4) ||type.equals(9) ||type.equals(10) ||type.equals(11)){
            if (StringUtils.isEmpty(detentionName)){
                detentionName=null;
            }
            List<AppointmentSettings> appointmentSettings1 = appointmentSettingsRepository.findAll(Specifications.<AppointmentSettings>and()
                    .eq("type", type)
                    .eq("detentionName", detentionName)
                    .build(),Sort.by(Sort.Order.desc("updateTime")));

            return appointmentSettings1;
        }
        if(type.equals(3)){
            int week = cal.get(Calendar.DAY_OF_WEEK)-1;
            List<AppointmentSettings> all = appointmentSettingsRepository.findAll(Specifications.<AppointmentSettings>and()
                    .eq("type", type)
                    .eq("detentionName", detentionName)
                    .eq("serialNumber", week)
                    .build(),Sort.by(Sort.Order.asc("period")));
            for(int i=0;i<all.size();i++){
                String start=data + " "+all.get(i).getPeriod()+":00";
                String end = data + " "+all.get(i).getPeriod()+":59";
                Integer accessNumberCount = accessMapper.getAccessNumberCount(start, end,detentionName);
                Integer peopleNumber=all.get(i).getPeopleNumber()-accessNumberCount;
                if(peopleNumber<0){
                    peopleNumber=0;
                }
                all.get(i).setPeopleNumber(peopleNumber);
            }
            return all;
        }
        if(type.equals(7)){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(cal.getTime());
            Integer accessNumberCountByType = accessMapper.getAccessNumberCountByType(date + " 00:00:00", date + " 23:59:59", Access.Type.ONLINE);
            Integer allNumber=0;
            AppointmentSettings appointmentSettingsAll = appointmentSettingsRepository.findOne(Specifications.<AppointmentSettings>and()
                    .eq("type", 7)
                    .eq("presenceStatus", 1).build()).orElse(null);
            if(!StringUtils.isEmpty(appointmentSettingsAll)){
                allNumber=appointmentSettingsAll.getPeopleNumber();
            }
            int week = cal.get(Calendar.DAY_OF_WEEK)-1;
            List<AppointmentSettings> all = appointmentSettingsRepository.findAll(Specifications.<AppointmentSettings>and()
                    .eq("type", 3)
                    .eq("serialNumber", week).build(),Sort.by(Sort.Order.asc("period")));
            for(int i=0;i<all.size();i++){
                String start=data + " "+all.get(i).getPeriod()+":00";
                String end = data + " "+all.get(i).getPeriod()+":59";
                Integer accessNumberCount = accessMapper.getAccessNumberCount(start, end,detentionName);
                Integer peopleNumberIs=all.get(i).getPeopleNumber()-accessNumberCount;
                Integer peopleNumber=allNumber-accessNumberCountByType;
                if(peopleNumberIs<peopleNumber){
                    peopleNumber=peopleNumberIs;
                }
                if(peopleNumber<0){
                    peopleNumber=0;
                }
                all.get(i).setPeopleNumber(peopleNumber);
            }
            return all;
        }
        if(type.equals(6)){
            appointmentSettings=appointmentSettings = appointmentSettingsRepository.findAll(Specifications.<AppointmentSettings>and()
                    .eq("type", type).build(),Sort.by(Sort.Order.asc("serialNumber")));
        }
        return appointmentSettings;
    }

    /**
     * 新增编辑预约设置
     * @param appointmentSettings
     * @return
     */
    public AppointmentSettings insertAppointmentSettings(AppointmentSettings appointmentSettings){
        appointmentSettingsRepository.save(appointmentSettings);
        return appointmentSettings;
    }

    public void contactDetailsSort(List<AppointmentSettings> appointmentSettingses){
        appointmentSettingsRepository.saveAll(appointmentSettingses);
    }

    /**
     * 删除预约设置
     * @param ids
     */
    public void deleteAppointmentSettings(String ids){
        Integer[] integers = StringUtil.toIntArray(ids);
        for (Integer i:integers){
            AppointmentSettings appointmentSettings = appointmentSettingsRepository.findOne(Specifications.<AppointmentSettings>and().eq("id", i).build()).orElse(null);
            appointmentSettingsRepository.deleteById(i);
            if(appointmentSettings!=null){
                if(appointmentSettings.getType().equals(6)){
                    appointmentSettingsMapper.deleteContactDetails(appointmentSettings.getSerialNumber());
                }
                appointmentSettingsMapper.deleteByTypePeriod(3,appointmentSettings.getPeriod());
            }
        }
    }

    /**
     * 小程序添加预约信息
     * @param insertAccessRequest
     */
    public void wechatMiniInsertAccess(InsertAccessRequest insertAccessRequest) throws ParseException {
        Attorney attorney = attorneyRepository.findOne(Specifications.<Attorney>and()
                .eq("id", getCurrentUser().getId())
                .eq("presenceStatus", 1).build()).orElse(null);
        if(StringUtils.isEmpty(attorney)){
            APIError.e(400,"律师不存在");
        }
        if(!attorney.getCertification().equals(Certification.PASS)){
            APIError.e(40000,"律师身份认证未通过之前不能预约");
        }


//        if(insertAccessRequest.getType().equals(Access.Type.ONLINE)){
//            List<Access> accesses = accessRepository.findAll(Specifications.<Access>and()
//                    .eq("presenceStatus", 1)
//                    .eq("attorneyId", getCurrentUser().getId())
//                    .eq("suspectsName", insertAccessRequest.getSuspectsName()).build());
//            if(accesses.size()==0){
//                APIError.e(400,"第一次预约该被访问人不能是远程会见");
//            }
//        }
//----  验证人数是否被预约满
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(insertAccessRequest.getAppointmentTime().getTime());
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int hours = insertAccessRequest.getAppointmentTime().getHours();
        int minutes = insertAccessRequest.getAppointmentTime().getMinutes();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(calendar.getTime());
        StringBuffer period=new StringBuffer();
        String detentionName = insertAccessRequest.getDetentionCenterName();
        if(hours<10){
            period.append("0");
        }
        period.append(hours);
        period.append(":");
        if(minutes<10){
            period.append("0");
        }
        period.append(minutes);
        AppointmentSettings appointmentSettings = appointmentSettingsRepository.findOne(Specifications.<AppointmentSettings>and()
                .eq("type", 3)
                .eq("serialNumber", week)
                .eq("detentionName", detentionName)
                .eq("period", period.toString()).build()).orElse(null);
        String start= date + " "+period+":00";
        String end = date + " "+period+":59";
        Integer accessNumberCount = accessMapper.getAccessNumberCount(start, end,detentionName);
        Integer peopleNumber=appointmentSettings.getPeopleNumber()-accessNumberCount;
        if(peopleNumber<=0){
            APIError.e(400,"该时间段预约人数已满");
        }
//-------
//----  验证远程预约是否还有席位
        if(insertAccessRequest.getType()==Access.Type.ONLINE){
            AppointmentSettings appointmentSettings2 = appointmentSettingsRepository.findOne(Specifications.<AppointmentSettings>and()
                    .eq("type", 7)
                    .eq("presenceStatus", 1).build()).orElse(null);
            if(!StringUtils.isEmpty(appointmentSettings2)){
                Integer accessNumberCountByType = accessMapper.getAccessNumberCountByType(date + " 00:00:00", date + " 23:59:59", Access.Type.ONLINE);
                if(accessNumberCountByType>=appointmentSettings2.getPeopleNumber()){
                    APIError.e(400,"今日远程预约席位已满");
                }
            }
        }
//---
//----  预约规则
        AppointmentSettings appointmentSettings3 = appointmentSettingsRepository.findOne(Specifications.<AppointmentSettings>and()
                .eq("type", 8)
                .eq("presenceStatus", 1).build()).orElse(null);
        if(!StringUtils.isEmpty(appointmentSettings3)){
            if(appointmentSettings3.getSerialNumber().equals(1)){
                List<Access> all = accessRepository.findAll(Specifications.<Access>and()
                        .eq("attorneyId", getCurrentUser().getId())
                        .eq("presenceStatus", 1)
                        .ne("certification",Certification.REJECTED)
                        .between("appointmentTime",
                                new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 00:00:00").getTime()),
                                new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 23:59:59").getTime())).build());
                if(all.size()>0){
                    APIError.e(400,"律师每天只能预约一次,您已经预约过了！");
                }
            }
            if(appointmentSettings3.getSerialNumber().equals(2)){
                String specHoursStart=null;
                String specHoursEnd=null;
                if(hours<=12){
                    specHoursStart=" 00:00:00";
                    specHoursEnd=" 12:00:00";
                }else {
                    specHoursStart=" 12:00:00";
                    specHoursEnd=" 23:59:59";
                }
                List<Access> all = accessRepository.findAll(Specifications.<Access>and()
                        .eq("attorneyId", getCurrentUser().getId())
                        .eq("presenceStatus", 1)
                        .ne("certification",Certification.REJECTED)
                        .between("appointmentTime",
                                new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + specHoursStart).getTime()),
                                new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + specHoursEnd).getTime())).build());
                if(all.size()>0){
                    APIError.e(400,"律师每天上午只能预约一次，下午只能预约一次,您已经预约过了！");
                }
            }
        }
//---
//----  验证当日不能预约当日
        Date today=new Date();
        today.setTime(insertAccessRequest.getAppointmentTime().getTime());
        if(simpleDateFormat.format(new Date()).equals(simpleDateFormat.format(today))){
            APIError.e(400,"当日不能预约当日！");
        }
//---
        Access access=new Access();
        access.setAttorneyId(getCurrentUser().getId());
        access.setAttorneyName(getCurrentUser().getName());
        access.setType(insertAccessRequest.getType());
        access.setCertification(Certification.UNREVIEWED);
        access.setAppointmentTime(insertAccessRequest.getAppointmentTime());
        access.setDetentionCenterId(insertAccessRequest.getDetentionCenterId());
        access.setDetentionCenterName(insertAccessRequest.getDetentionCenterName());
        access.setSuspectsName(insertAccessRequest.getSuspectsName());
        access.setNumberplate(insertAccessRequest.getNumberplate());
        access.setIsFirst(insertAccessRequest.isFirst());
        access.setOrderQuantity(insertAccessRequest.getOrderQuantity());
        access.setAccessStatus(Access.AccessStatus.NOTVISITED);
        access.setLawsuitStatus(insertAccessRequest.getLawsuitStatus());  // 增加诉讼状态
        access.setBirthDay(insertAccessRequest.getBirthDay());
        accessRepository.save(access);
        List<Qualification> qualifications = insertAccessRequest.getQualifications();
        for(Qualification qualification:qualifications){
            qualification.setAccessId(access.getId());
            qualificationRepository.save(qualification);
        }
    }

    /**
     * 微信获取预约信息
     * @param obtainAccessRequest
     * @return
     */
    public List<Access> wechatObtainAccess(ObtainAccessRequest obtainAccessRequest){
        List<Access> accesses=new ArrayList<>();
        PageHelper.startPage(obtainAccessRequest.getPageNum(),obtainAccessRequest.getPageSize());
        Integer attorneyId=getCurrentUser().getId();
        if(obtainAccessRequest.getCertification()!=null){
             accesses = accessMapper.getAccessListByAidTypeAt(attorneyId, obtainAccessRequest.getType(), obtainAccessRequest.getCertification());
        }else {
            accesses = accessMapper.getAccessListByAidType(attorneyId, obtainAccessRequest.getType());
        }
        for(int i=0;i<accesses.size();i++){
            accesses.get(i).setQualifications1(qualificationRepository.findAll(Specifications.<Qualification>and().eq("type",2).eq("accessId",accesses.get(i).getId()).build()));
            accesses.get(i).setQualifications2(qualificationRepository.findAll(Specifications.<Qualification>and().eq("type",3).eq("accessId",accesses.get(i).getId()).build()));
        }
        return accesses;
    }

    /**
     * 取消预约
     * @param accessId
     */
    public void cancelAccess(Integer accessId){
        Access access = accessRepository.findById(accessId).orElse(null);
        if(!StringUtils.isEmpty(access)){
            if(access.getAppointmentTime().getTime() < Calendar.getInstance().getTime().getTime()){
                APIError.e(40000,"预约时间已过不能取消预约");
            }
            if(!access.getCertification().equals(Certification.UNREVIEWED)){
                APIError.e(40000,"审核过的预约不能取消");
            }
        }
        accessMapper.cancelAccess(Certification.CANCEL,accessId);
    }

    /**
     * 导出预约记录excel
     * @param start
     * @param end
     * @return
     */
    public String exportAccessExcel(String start,String end,Integer detentionCenterId){
        String name= "预约记录"+ System.currentTimeMillis()+ ".xlsx";
        String excelUrl= projectConfig.getUploadPath() + File.separator + "excel" + File.separator + name;
        ExcelWriter excelWriter = EasyExcel.write(excelUrl, AccessExcel.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
        List<AccessExcel> accessExcels = new ArrayList<>();
        if(StringUtils.isEmpty(detentionCenterId)){
           accessExcels = accessMapper.exportAccessExcel(start, end);
        }else {
            accessExcels =accessMapper.exportAccessExcel2(start, end,detentionCenterId);
        }
        for(int i=0;i<accessExcels.size();i++){
            AccessExcel accessExcel = accessExcels.get(i);
            Attorney attorney = attorneyRepository.findOne(Specifications.<Attorney>and().eq("id", accessExcel.getAttorneyId()).build()).orElse(null);
            accessExcels.get(i).setNumber(i+1);
            if(attorney!=null){
                accessExcels.get(i).setIdNumber(attorney.getIdNumber());
                accessExcels.get(i).setLicenseNumber(attorney.getLicenseNumber());
                accessExcels.get(i).setLawOffice(attorney.getLawOffice());
                accessExcels.get(i).setPhone(attorney.getPhone());
            }

            if(accessExcel.getAppointmentTime()!=null){
                accessExcels.get(i).setAppointmentTimeExcel(new Date(accessExcel.getAppointmentTime().getTime()));
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(accessExcel.getAppointmentTime());
                accessExcels.get(i).setDate(date);
                StringBuffer period=new StringBuffer();
                int hours = accessExcel.getAppointmentTime().getHours();
                int minutes = accessExcel.getAppointmentTime().getMinutes();
                if(hours<=12){
                    period.append("上午");
                }else {
                    period.append("下午");
                }
                period.append(hours+":"+minutes+"时段");
                accessExcels.get(i).setPeriod(period.toString());
            }
            if(accessExcel.getVisitTime()!=null){
                accessExcels.get(i).setVisitTimeExcel(new Date(accessExcel.getVisitTime().getTime()));
            }
            if(accessExcel.getIsFirst()){
                accessExcels.get(i).setIsFirstExcel("是");
            }else {
                accessExcels.get(i).setIsFirstExcel("否");
            }
            switch (accessExcel.getCertification()){
                case UNREVIEWED:
                    accessExcels.get(i).setCertificationExcel("待审核");
                    break;
                case PASS:
                    accessExcels.get(i).setCertificationExcel("已通过");
                    break;
                case REJECTED:
                    accessExcels.get(i).setCertificationExcel("已拒绝");
                    break;
                case CANCEL:
                    accessExcels.get(i).setCertificationExcel("已取消");
                    break;
                default:
                    System.out.println("未知");
            }
            switch (accessExcel.getAccessStatus()){
                case NOTVISITED:
                    accessExcels.get(i).setAccessStatusExcel("未到访");
                    break;
                case VISITED:
                    accessExcels.get(i).setAccessStatusExcel("已到访");
                    break;
                case END:
                    accessExcels.get(i).setAccessStatusExcel("会面结束");
                    break;
                default:
                    System.out.println("未知");
            }
            switch (accessExcel.getType()){
                case OFFLINE:
                    accessExcels.get(i).setTypeExcel("预约会见");
                    break;
                case ONLINE:
                    accessExcels.get(i).setTypeExcel("远程会见");
                    break;
                default:
                    System.out.println("未知");
            }

        }
        excelWriter.write(accessExcels, writeSheet);
        excelWriter.finish();
        return "excel/"+name;
    }

    /**
     * 7天时间是否是工作日
     * @return List<WorkingDayResponse>
     */
    public List<WorkingDayResponse> workingDay(){
        List<WorkingDayResponse> workingDayResponses=new ArrayList<>();
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        for(int i=0;i<7;i++){
            Date date=calendar.getTime();
            WorkingDayResponse workingDayResponse=new WorkingDayResponse();
            workingDayResponse.setDate(simpleDateFormat.format(date));
            Integer peopleNumber = appointmentSettingsMapper.weekTotalPeopleNumber(calendar.get(Calendar.DAY_OF_WEEK) - 1);
            if(peopleNumber>0){
                workingDayResponse.setWork(true);
            }
            workingDayResponses.add(workingDayResponse);
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        return workingDayResponses;
    }

    /**
     * 获取管理员管理单位
     * @return
     */
    public ManagerDetentionCenter obtainManagerDetentionCenter(ObtainManagerDetentionCenterReq obtainManagerDetentionCenterReq){
        if(StringUtils.isEmpty(obtainManagerDetentionCenterReq.getManagerId())){
            obtainManagerDetentionCenterReq.setManagerId(getManager().getId());
        }
        ManagerDetentionCenter managerDetentionCenter = managerDetentionCenterRepository.findOne(Specifications.<ManagerDetentionCenter>and()
                .eq("managerId", obtainManagerDetentionCenterReq.getManagerId()).build()).orElse(null);
        return managerDetentionCenter;
    }

    /**
     * 添加管理员管理单位
     * @param insertManagerDetentionCenterReq
     * @return
     */
    public ManagerDetentionCenter insertManagerDetentionCenter(InsertManagerDetentionCenterReq insertManagerDetentionCenterReq){
        ManagerDetentionCenter managerDetentionCenter = managerDetentionCenterRepository.findOne(Specifications.<ManagerDetentionCenter>and()
                .eq("managerId", insertManagerDetentionCenterReq.getManagerId()).build()).orElse(null);
        if(StringUtils.isEmpty(managerDetentionCenter)){
            managerDetentionCenter=new ManagerDetentionCenter();
        }
        managerDetentionCenter.setDetentionCenterId(insertManagerDetentionCenterReq.getDetentionCenterId());
        managerDetentionCenter.setDetentionCenterName(insertManagerDetentionCenterReq.getDetentionCenterName());
        managerDetentionCenter.setManagerId(insertManagerDetentionCenterReq.getManagerId());
        managerDetentionCenterRepository.save(managerDetentionCenter);
        return managerDetentionCenter;
    }


    /**
     * 发送模板消息
     * @param toUser
     * @param suspectsName
     * @param detentionCenterName
     * @param appointmentTime
     * @param certification
     * @return WxMaSubscribeMessage
     */
    private WxMaSubscribeMessage getWxMaSubscribeMessage(String toUser,String suspectsName,String detentionCenterName,String appointmentTime,String certification){
        WxMaSubscribeMessage wxMaSubscribeMessage=new WxMaSubscribeMessage();
        wxMaSubscribeMessage.setTemplateId("kQ6qo64ntzDW3rT9g-j55r3e_UGJskR1m8UcpKE7F5c");
        wxMaSubscribeMessage.setToUser(toUser);
        wxMaSubscribeMessage.addData(new WxMaSubscribeData("name3",suspectsName));
        wxMaSubscribeMessage.addData(new WxMaSubscribeData("thing2",detentionCenterName));
        wxMaSubscribeMessage.addData(new WxMaSubscribeData("date4",appointmentTime));
        wxMaSubscribeMessage.addData(new WxMaSubscribeData("phrase6",certification));
        return wxMaSubscribeMessage;
    }



}

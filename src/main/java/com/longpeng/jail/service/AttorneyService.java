package com.longpeng.jail.service;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeData;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.cq1080.bean.form.PageForm;
import com.cq1080.rest.APIError;
import com.cq1080.utils.StringUtil;
import com.cq1080.utils.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import com.github.wenhao.jpa.Specifications;
import com.longpeng.jail.bean.entity.*;
import com.longpeng.jail.bean.excel.AttorneyExcel;
import com.longpeng.jail.bean.request.InsertQualificationRequest;
import com.longpeng.jail.bean.request.ObtainQualificationRequest;
import com.longpeng.jail.bean.request.WechatMiniRegisteredRequest;
import com.longpeng.jail.bean.response.ObtainQualificationResponse;
import com.longpeng.jail.config.ProjectConfig;
import com.longpeng.jail.datasource.mapper.AccessMapper;
import com.longpeng.jail.datasource.mapper.AttorneyMapper;
import com.longpeng.jail.datasource.mapper.VerificationCodeMapper;
import com.longpeng.jail.datasource.repository.AppointmentSettingsRepository;
import com.longpeng.jail.datasource.repository.AttorneyRepository;
import com.longpeng.jail.datasource.repository.QualificationRepository;
import com.longpeng.jail.datasource.repository.VerificationCodeRepository;
import com.longpeng.jail.redis.RedisCache;
import com.longpeng.jail.util.Captcha;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.cq1080.jpa.specification.SpecificationUtil;
import org.springframework.util.StringUtils;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AttorneyService extends BaseService{

    private AttorneyRepository attorneyRepository;
    private AttorneyMapper attorneyMapper;
    private QualificationRepository qualificationRepository;
    private WechatMiniappService wechatMiniappService;
    private RedisCache redisCache;
    private AccessMapper accessMapper;
    private AppointmentSettingsRepository appointmentSettingsRepository;
    private ProjectConfig projectConfig;
    private VerificationCodeRepository verificationCodeRepository;
    private SmsService smsService;
    private VerificationCodeMapper verificationCodeMapper;

    @Autowired
    public AttorneyService(AttorneyRepository attorneyRepository, AttorneyMapper attorneyMapper, QualificationRepository qualificationRepository, WechatMiniappService wechatMiniappService, RedisCache redisCache, AccessMapper accessMapper, AppointmentSettingsRepository appointmentSettingsRepository, ProjectConfig projectConfig, VerificationCodeRepository verificationCodeRepository, SmsService smsService, VerificationCodeMapper verificationCodeMapper) {
        this.attorneyRepository = attorneyRepository;
        this.attorneyMapper = attorneyMapper;
        this.qualificationRepository = qualificationRepository;
        this.wechatMiniappService = wechatMiniappService;
        this.redisCache = redisCache;
        this.accessMapper = accessMapper;
        this.appointmentSettingsRepository = appointmentSettingsRepository;
        this.projectConfig = projectConfig;
        this.verificationCodeRepository = verificationCodeRepository;
        this.smsService = smsService;
        this.verificationCodeMapper = verificationCodeMapper;
    }

    /**
     * 获取律师列表
     * @param attorney
     * @param pageForm
     * @return
     */
    public Page<Attorney> getAttorneys(Attorney attorney, PageForm pageForm){
        Page<Attorney> attorneys = attorneyRepository.findAll(SpecificationUtil.<Attorney>filter(attorney, pageForm).ne("name",null).build(), pageForm.pageRequest());
        for(int i=0;i<attorneys.getContent().size();i++){
            attorneys.getContent().get(i).setViolationNumber(accessMapper.getViolationNumberByAid(attorneys.getContent().get(i).getId())+attorneys.getContent().get(i).getArtificialViolation());
            attorneys.getContent().get(i).setQualifications(qualificationRepository.findAll(Specifications.<Qualification>and().eq("presenceStatus",1)
                    .eq("type",1)
                    .eq("attorneyId",attorneys.getContent().get(i).getId()).build()));
        }
        return attorneys;
    }

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    public String insertVerificationCode(String phone){
        VerificationCode verificationCode=new VerificationCode();
        verificationCode.setPhone(phone);
        verificationCode.setCode(Captcha.generateRandomDigit(6));
        verificationCodeRepository.save(verificationCode);
        try {
            smsService.send(verificationCode.getPhone(),verificationCode.getCode());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("验证码发送失败");
        }
        return verificationCode.getCode();
    }

    /**
     * 编辑律师列表
     * @param attorney
     * @return
     */
    public Attorney insertAttorney(Attorney attorney){
        attorneyRepository.save(attorney);
        return attorney;
    }

    /**
     * 更新审核列表
     * @param attorney
     */
    public void updateAttorney(Attorney attorney){
        attorneyRepository.save(attorney);
        attorneyMapper.updateQualificationByAid(attorney.getId());
        List<Qualification> qualifications = attorney.getQualifications();
        for(Qualification qualification:qualifications){
            qualification.setAttorneyId(attorney.getId());
            qualification.setType(1);
            qualificationRepository.save(qualification);
        }

    }

    /**
     * 删除律师
     * @param ids
     */
    public void deleteAttorneys(String ids){
        Integer[] integers = StringUtil.toIntArray(ids);
        for(Integer i:integers){
            attorneyMapper.deleteAttorneyById(i);
        }
    }

    /**
     * 审核律师资料
     * @param insertQualificationRequest
     */
    public void insertQualification(InsertQualificationRequest insertQualificationRequest){
        Attorney attorney = attorneyRepository.findOne(Specifications.<Attorney>and().eq("id", insertQualificationRequest.getAttorneyId()).build()).orElse(null);
        if(StringUtils.isEmpty(attorney)){
            APIError.INVALID_REQ();
        }
        attorney.setCertification(insertQualificationRequest.getCertification());
        attorneyRepository.save(attorney);
        try {
            WxMaSubscribeMessage wxMaSubscribeMessage = getWxMaSubscribeMessage(attorney.getOpenId(), Certification.enumToString(attorney.getCertification()));
            wechatMiniappService.sendSubscribeMsg(wxMaSubscribeMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取律师审核列表
     * @param obtainQualificationRequest
     * @return
     */
    public List<ObtainQualificationResponse> obtainQualification(ObtainQualificationRequest obtainQualificationRequest){
        PageHelper.startPage(obtainQualificationRequest.getPageNum(),obtainQualificationRequest.getPageSize());
        List<ObtainQualificationResponse> obtainQualificationResponses = attorneyMapper.getObtainQualificationResponses(obtainQualificationRequest);
        for(int i=0;i<obtainQualificationResponses.size();i++){
            obtainQualificationResponses.get(i).setQualifications(qualificationRepository.findAll(Specifications.<Qualification>and()
                    .eq("attorneyId",obtainQualificationResponses.get(i).getAttorneyId())
                    .eq("type",1).eq("presenceStatus",1).build()));
        }
        return obtainQualificationResponses;
    }

    /**
     * 微信小程序登陆
     * @param code
     * @return
     */
    public Attorney wechatMiniLogin(String code){
        WxMaJscode2SessionResult login = wechatMiniappService.login(code);
        String openid = login.getOpenid();
        Attorney attorney = attorneyRepository.findOne(Specifications.<Attorney>and().eq("openId", openid).eq("presenceStatus",1).build()).orElse(null);
        if(attorney==null){
            attorney=new Attorney();
            attorney.setOpenId(openid);
            attorney.setPresenceStatus(1);
            attorneyRepository.save(attorney);
        }
        attorney.setToken(TokenUtil.generateToken());
        redisCache.setCacheObject(attorney.getToken(), attorney,3600, TimeUnit.SECONDS);
        return attorney;
    }

    /**
     * 用手机登录并更换绑定的微信号
     * @param phone
     * @param phoneCode
     * @return
     */
    public Attorney wechatMiniPhoneLogin(String phone,String phoneCode){
        Attorney oldAttorney = getCurrentUser();
        Attorney attorney = attorneyRepository.findOne(Specifications.<Attorney>and()
                .eq("phone", phone)
                .eq("presenceStatus", 1).build()).orElse(null);
        if(StringUtils.isEmpty(attorney)){
            APIError.e(40000,"该手机号没有注册");
        }
        VerificationCode verificationCode = verificationCodeMapper.getVerificationCode(phone);
        if(StringUtils.isEmpty(verificationCode) || !verificationCode.getCode().equals(phoneCode)){
            APIError.e(400,"验证码无效");
        }
        attorney.setOpenId(oldAttorney.getOpenId());
        attorneyRepository.save(attorney);
        attorneyMapper.deleteAttorneyById(oldAttorney.getId());
        redisCache.setCacheObject(oldAttorney.getToken(),attorney);
        return attorney;
    }

    /**
     * 微信小层序注册
     * @param wechatMiniRegisteredRequest
     * @return
     */
    public Attorney wechatMiniRegistered(WechatMiniRegisteredRequest wechatMiniRegisteredRequest){
        List<Attorney> all = attorneyRepository.findAll(Specifications.<Attorney>and()
                .eq("phone", wechatMiniRegisteredRequest.getPhone())
                .eq("presenceStatus", 1).build());
        if(all.size()>0){
            APIError.e(40000,"手机号已经被注册");
        }
        List<Attorney> allByIdNumber = attorneyRepository.findAll(Specifications.<Attorney>and()
                .eq("idNumber", wechatMiniRegisteredRequest.getIdNumber())
                .eq("presenceStatus", 1).build());
        if(allByIdNumber.size()>0){
            APIError.e(40000,"身份证已经被注册");
        }
        // 验证码校验
        /*VerificationCode verificationCode = verificationCodeMapper.getVerificationCode(wechatMiniRegisteredRequest.getPhone());
        if(StringUtils.isEmpty(verificationCode) || !verificationCode.getCode().equals(wechatMiniRegisteredRequest.getCode())){
            APIError.e(400,"验证码无效");
        }*/
        Attorney attorney = getCurrentUser();
        attorney.setWechatAvatar(wechatMiniRegisteredRequest.getWechatAvatar());
        attorney.setGender(wechatMiniRegisteredRequest.getGender());
        attorney.setPhone(wechatMiniRegisteredRequest.getPhone());
        attorney.setName(wechatMiniRegisteredRequest.getName());
        attorney.setIdNumber(wechatMiniRegisteredRequest.getIdNumber());
        attorney.setLicenseNumber(wechatMiniRegisteredRequest.getLicenseNumber());
        attorney.setFaceImg(wechatMiniRegisteredRequest.getFaceImg());
        attorney.setLawOffice(wechatMiniRegisteredRequest.getLawOffice());
        attorney.setCertification(Certification.UNREVIEWED);
        attorney.setArtificialViolation(0);
        attorneyRepository.save(attorney);
        redisCache.setCacheObject(attorney.getToken(),attorney);
        List<Qualification> qualifications = wechatMiniRegisteredRequest.getQualifications();
        for(Qualification qualification:qualifications){
            qualification.setAttorneyId(attorney.getId());
            qualification.setType(1);
            qualificationRepository.save(qualification);
        }
        return attorney;
    }

    /**
     * 微信小程序实时获取用户信息
     * @return
     */
    public Attorney getInfo(){
        Attorney attorney = attorneyRepository.findOne(Specifications.<Attorney>and()
                .eq("id", getCurrentUser().getId())
                .eq("presenceStatus", 1).build()).orElse(null);
        if(attorney==null){
            APIError.NEED_LOGIN();
        }
        List<Qualification> qualifications = qualificationRepository.findAll(Specifications.<Qualification>and()
                .eq("attorneyId", attorney.getId())
                .eq("type", 1)
                .eq("presenceStatus", 1).build());
        attorney.setQualifications(qualifications);
        attorney.setViolationNumber(accessMapper.getViolationNumberByAid(attorney.getId())+attorney.getArtificialViolation());
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        if(attorney.getViolationNumber()>0){
            AppointmentSettings appointmentSettings = appointmentSettingsRepository.findOne(Specifications.<AppointmentSettings>and()
                    .eq("type", 5)
                    .eq("serialNumber", attorney.getViolationNumber())
                    .eq("presenceStatus", 1).build()).orElse(null);
            if(appointmentSettings==null){
                attorney.setViolationDate(year+"-12-31 23:59:59");
            }else {
                Timestamp appointmentTime = accessMapper.getAppointmentTime(attorney.getId());
                Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(appointmentTime.getTime());
                calendar.add(Calendar.DAY_OF_MONTH,appointmentSettings.getPeopleNumber());
                Date date1 = calendar.getTime();
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd 23:59:59");
                attorney.setViolationDate(simpleDateFormat.format(date1));
            }
        }else {
            attorney.setViolationDate(year+"-01-01 00:00:00");

        }
        redisCache.setCacheObject((String) getHeader("token"),attorney);
        return attorney;
    }


    /**
     * 导出律师库excel
     * @return
     */
    public String exportAttorneyExcel(){
        String name= "律师库统计"+ System.currentTimeMillis()+ ".xlsx";
        String excelUrl= projectConfig.getUploadPath() + File.separator + "excel" + File.separator + name;
        ExcelWriter excelWriter = EasyExcel.write(excelUrl, AttorneyExcel.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
        List<AttorneyExcel> attorneyExcels=attorneyMapper.exportAttorneyExcel();
        for(int i=0;i<attorneyExcels.size();i++){
            Date date=new Date(attorneyExcels.get(i).getCreateTime().getTime());
            attorneyExcels.get(i).setDate(date);
            attorneyExcels.get(i).setAccessNumber(accessMapper.getAccessNumberByAid(attorneyExcels.get(i).getId()));
            attorneyExcels.get(i).setViolationNumber(accessMapper.getViolationNumberByAid(attorneyExcels.get(i).getId())+attorneyExcels.get(i).getArtificialViolation());
            if(attorneyExcels.get(i).getGender().equals(Gender.MALE)){
                attorneyExcels.get(i).setSex("男");
            }
            if("MALE".equals(attorneyExcels.get(i).getGender())){
                attorneyExcels.get(i).setSex("男");
            }
            if("FEMALE".equals(attorneyExcels.get(i).getGender())){
                attorneyExcels.get(i).setSex("女");
            }
        }
        excelWriter.write(attorneyExcels, writeSheet);
        excelWriter.finish();
        return "excel/"+name;
    }

    /**
     * 重新提交律师审核资料
     * @param wechatMiniRegisteredRequest
     */
    public Attorney review(WechatMiniRegisteredRequest wechatMiniRegisteredRequest){
        Attorney attorney = getCurrentUser();
        attorney.setWechatAvatar(wechatMiniRegisteredRequest.getWechatAvatar());
        attorney.setGender(wechatMiniRegisteredRequest.getGender());
        attorney.setPhone(wechatMiniRegisteredRequest.getPhone());
        attorney.setName(wechatMiniRegisteredRequest.getName());
        attorney.setIdNumber(wechatMiniRegisteredRequest.getIdNumber());
        attorney.setLicenseNumber(wechatMiniRegisteredRequest.getLicenseNumber());
        attorney.setFaceImg(wechatMiniRegisteredRequest.getFaceImg());
        attorney.setLawOffice(wechatMiniRegisteredRequest.getLawOffice());
        attorney.setCertification(Certification.UNREVIEWED);
        attorney.setArtificialViolation(0);
        attorney.setToken((String) getHeader("token"));
        attorneyRepository.save(attorney);
        redisCache.setCacheObject(attorney.getToken(),attorney);
        attorneyMapper.updateQualificationByAid(attorney.getId());
        List<Qualification> qualifications = wechatMiniRegisteredRequest.getQualifications();
        for(Qualification qualification:qualifications){
            qualification.setAttorneyId(attorney.getId());
            qualification.setType(1);
            qualificationRepository.save(qualification);
        }
        return attorney;
    }

    /**
     * 消息模板
     * @param toUser
     * @param certification
     * @return
     */
    private WxMaSubscribeMessage getWxMaSubscribeMessage(String toUser, String certification){
        WxMaSubscribeMessage wxMaSubscribeMessage=new WxMaSubscribeMessage();
        wxMaSubscribeMessage.setTemplateId("-jeAK0_NWw9G4OluNr0-Tyqubjjmaqx9MpRUibjpSC4");
        wxMaSubscribeMessage.setToUser(toUser);
        wxMaSubscribeMessage.addData(new WxMaSubscribeData("thing1","律师资质审核"));
        wxMaSubscribeMessage.addData(new WxMaSubscribeData("date2",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        wxMaSubscribeMessage.addData(new WxMaSubscribeData("phrase5",certification));
        return wxMaSubscribeMessage;
    }

}

package com.longpeng.jail.controller.api;

import com.cq1080.auth.annotation.PermissionDes;
import com.cq1080.rest.API;
import com.longpeng.jail.bean.entity.AppointmentSettings;
import com.longpeng.jail.bean.entity.Attorney;
import com.longpeng.jail.bean.entity.Carousel;
import com.longpeng.jail.bean.entity.Qualification;
import com.longpeng.jail.bean.request.WechatMiniRegisteredRequest;
import com.longpeng.jail.service.AccessService;
import com.longpeng.jail.service.AttorneyService;
import com.longpeng.jail.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user/")
@Api(tags = "小程序用户模块")
public class WechatUserController {

    private AttorneyService attorneyService;
    private StatisticsService statisticsService;
    private AccessService accessService;

    @Autowired
    public WechatUserController(AttorneyService attorneyService, StatisticsService statisticsService, AccessService accessService) {
        this.attorneyService = attorneyService;
        this.statisticsService = statisticsService;
        this.accessService = accessService;
    }

    @GetMapping("login")
    @ApiOperation("登陆")
    public API<Attorney> login(@RequestParam("code")String code){
        Attorney attorney = attorneyService.wechatMiniLogin(code);
        return API.ok(attorney);
    }

    @GetMapping("phoneLogin")
    @ApiOperation("手机号登录")
    public API<Attorney> phoneLogin(@RequestParam("phone")String phone,@RequestParam("phoneCode")String phoneCode){
        Attorney attorney = attorneyService.wechatMiniPhoneLogin(phone,phoneCode);
        return API.ok(attorney);
    }

    @PostMapping("verificationCode")
    @ApiOperation("发送验证吗")
    public API<String> insertVerificationCode(@RequestBody String phone){
        String code = attorneyService.insertVerificationCode(phone);
        return API.ok(code);
    }

    @PostMapping("registered")
    @ApiOperation("注册")
    public API<Attorney> registered(@RequestBody WechatMiniRegisteredRequest wechatMiniRegisteredRequest){
        Attorney attorney = attorneyService.wechatMiniRegistered(wechatMiniRegisteredRequest);
        return API.ok(attorney);
    }

    @GetMapping("getInfo")
    @ApiOperation("更新用户信息")
    public API<Attorney> getInfo(){
        Attorney attorney = attorneyService.getInfo();
        return API.ok(attorney);
    }

    @GetMapping("carousel")
    @ApiOperation("获取轮播图")
    public API<List<Carousel>> getCarousels(){
        List<Carousel> carousels = statisticsService.getCarousels();
        return API.ok(carousels);
    }

    @GetMapping("appointmentSettings")
    @ApiOperation("获取联系人电话 ")
    @PermissionDes(menu = "联系人设置",name = "获取联系人电话")
    public API<List<AppointmentSettings>> obtainAppointmentSettings(){
        String detentionName ="";
        List<AppointmentSettings> appointmentSettings = accessService.obatinAppointmentSettings(6,detentionName);
        return API.ok(appointmentSettings);
    }

    @PostMapping("review")
    @ApiOperation("重新提交审核资料")
    public API<Attorney> insert(@RequestBody WechatMiniRegisteredRequest wechatMiniRegisteredRequest){
        Attorney attorney = attorneyService.review(wechatMiniRegisteredRequest);
        return API.ok(attorney);
    }

}

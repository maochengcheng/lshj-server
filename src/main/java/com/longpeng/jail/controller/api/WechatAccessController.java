package com.longpeng.jail.controller.api;

import com.cq1080.bean.form.PageForm;
import com.cq1080.rest.API;
import com.longpeng.jail.bean.entity.Access;
import com.longpeng.jail.bean.entity.AppointmentSettings;
import com.longpeng.jail.bean.entity.DetentionCenter;
import com.longpeng.jail.bean.request.InsertAccessRequest;
import com.longpeng.jail.bean.request.ObtainAccessRequest;
import com.longpeng.jail.bean.response.WorkingDayResponse;
import com.longpeng.jail.service.AccessService;
import com.longpeng.jail.service.DetentionCenterService;
import com.longpeng.jail.util.pageHelper.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("api/access/")
@Api(tags = "小程序预约相关")
public class WechatAccessController {

    private AccessService accessService;
    private DetentionCenterService detentionCenterService;

    @Autowired
    public WechatAccessController(AccessService accessService, DetentionCenterService detentionCenterService) {
        this.accessService = accessService;
        this.detentionCenterService = detentionCenterService;
    }

    @PostMapping("insertAccess")
    @ApiOperation(value = "添加预约")
    public API<String> insertAccess(@RequestBody InsertAccessRequest insertAccessRequest) throws ParseException {
        accessService.wechatMiniInsertAccess(insertAccessRequest);
        return API.ok("添加成功");
    }

    @GetMapping("workingDay")
    @ApiOperation(value = "7天的工作日")
    public API<List<WorkingDayResponse>> workingDay(){
        List<WorkingDayResponse> workingDayResponses = accessService.workingDay();
        return API.ok(workingDayResponses);
    }

    @GetMapping("obtainAccess")
    @ApiOperation(value = "获取预约信息")
    public API<PageResult<Access>> obtainAccess(ObtainAccessRequest obtainAccessRequest){
        List<Access> accesses = accessService.wechatObtainAccess(obtainAccessRequest);
        return API.ok(PageResult.of(accesses));
    }

    @GetMapping("appointmentSettings")
    @ApiOperation("获取预约设置 type 1-时间段 2-会见须知 3-时间段预约人数 4-温馨提示")
    public API<List<AppointmentSettings>> obtainAppointmentSettings(@RequestParam("type")Integer type,@RequestParam(value = "timestamp",required = false) Timestamp timestamp,@RequestParam(value = "data",required = false) String data,
                                                                    @RequestParam(value = "detentionName",required = false) String detentionName){
        if(2==type){
            detentionName="hjxz";
        }
        List<AppointmentSettings> appointmentSettings = accessService.wechatObatinAppointmentSettings(type,timestamp,data,detentionName);
        return API.ok(appointmentSettings);
    }

    @GetMapping("cancelAccess")
    @ApiOperation("取消预约")
    public API<String> cancelAccess(@RequestParam("accessId")Integer accessId){
        accessService.cancelAccess(accessId);
        return API.ok("取消成功");
    }

    @GetMapping("detentionCenter")
    @ApiOperation("获取单位列表")
    public API<Page<DetentionCenter>> getDetentionCenter(DetentionCenter detentionCenter, PageForm pageForm){
        Page<DetentionCenter> detentionCenters = detentionCenterService.getDetentionCenter(detentionCenter, pageForm);
        return API.ok(detentionCenters);
    }


}

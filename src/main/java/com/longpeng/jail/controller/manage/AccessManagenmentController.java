package com.longpeng.jail.controller.manage;

import com.cq1080.auth.annotation.PermissionDes;
import com.cq1080.auth.bean.Manager;
import com.cq1080.auth.service.AuthManagerService;
import com.cq1080.bean.form.PageForm;
import com.cq1080.meta.MetaUtils;
import com.cq1080.meta.bean.MetaData;
import com.cq1080.rest.API;
import com.longpeng.jail.bean.entity.Access;
import com.longpeng.jail.bean.entity.AppointmentSettings;
import com.longpeng.jail.bean.entity.DetentionCenter;
import com.longpeng.jail.bean.entity.ManagerDetentionCenter;
import com.longpeng.jail.bean.request.AccessBatchRequest;
import com.longpeng.jail.bean.request.InsertManagerDetentionCenterReq;
import com.longpeng.jail.bean.request.ObtainManagerDetentionCenterReq;
import com.longpeng.jail.service.AccessService;
import com.longpeng.jail.service.DetentionCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("manage/accessManagenment/")
@PermissionDes(menu = "预约管理")
@Api(tags = "预约管理")
public class AccessManagenmentController {

    private AccessService accessService;
    private DetentionCenterService detentionCenterService;

    @Autowired
    public AccessManagenmentController(AccessService accessService, DetentionCenterService detentionCenterService) {
        this.accessService = accessService;
        this.detentionCenterService = detentionCenterService;
    }

    @GetMapping("meta")
    @ApiOperation("配置元信息")
    public API<MetaData> userMeta() {
        return API.ok(MetaUtils.getMeta(Access.class));
    }

    @GetMapping("access")
    @PermissionDes(menu = "预约记录",name = "查看预约记录")
    @ApiOperation("获取预约记录列表")
    public API<Page<Access>> getAccess(Access access, PageForm pageForm){
        Page<Access> accesses = accessService.getAccesses(access, pageForm);
        return API.ok(accesses);
    }

//    @PostMapping("accessNote")
//    @PermissionDes(menu = "预约记录",name = "添加预约记录备注预约记录")
//    public API<String> insertAccessNote(@RequestBody Access access){
//        accessService.insertAccessNote(access);
//        return API.ok("成功");
//    }

    @GetMapping("managerDetentionCenter")
    @ApiOperation("获取管理员管理的单位")
    public API<ManagerDetentionCenter> obtainManagerDetentionCenter(ObtainManagerDetentionCenterReq obtainManagerDetentionCenterReq){
        ManagerDetentionCenter managerDetentionCenter = accessService.obtainManagerDetentionCenter(obtainManagerDetentionCenterReq);
        return API.ok(managerDetentionCenter);
    }

    @PostMapping("managerDetentionCenter")
    @ApiOperation("添加编辑管理员管理的单位")
    public API<String> insertManagerDetentionCenter(@RequestBody InsertManagerDetentionCenterReq insertManagerDetentionCenterReq){
        accessService.insertManagerDetentionCenter(insertManagerDetentionCenterReq);
        return API.ok("成功");
    }


    @PostMapping("access")
    @PermissionDes(menu = "预约记录",name = "编辑预约记录(审核)")
    @ApiOperation("审核")
    public API<String> insertAccess(@RequestBody Access access){
        accessService.insertAccess(access);
        return API.ok("成功");
    }

    @DeleteMapping("access")
    @PermissionDes(menu = "预约记录",name = "删除预约记录")
    @ApiOperation("删除预约记录")
    public API<String> deleteAccess(String ids){
        accessService.deleteAccesses(ids);
        return API.ok("删除成功");
    }

    @PostMapping("accessBatch")
    @PermissionDes(menu = "预约记录",name = "批量操作")
    @ApiOperation("批量操作")
    public API<String> accessBatch(@RequestBody List<AccessBatchRequest> accessBatchRequests){
        accessService.accessBatch(accessBatchRequests);
        return API.ok("成功");
    }

    @GetMapping("exportAccessExcel")
    @PermissionDes(menu = "预约记录",name = "导出预约记录excel")
    @ApiOperation("导出预约记录excel")
    public API<String> exportAccessExcel(@RequestParam("start")String start,@RequestParam("end")String end,@RequestParam(value = "detentionCenterId",required = false)Integer detentionCenterId){
        String accessExcelUrl = accessService.exportAccessExcel(start, end,detentionCenterId);
        return API.ok(accessExcelUrl);
    }

    @GetMapping("detentionCenter")
    @PermissionDes(menu = "预约记录",name = "获取单位列表")
    @ApiOperation("获取单位列表")
    public API<Page<DetentionCenter>> getDetentionCenter(DetentionCenter detentionCenter, PageForm pageForm){
        Page<DetentionCenter> detentionCenters = detentionCenterService.getDetentionCenter(detentionCenter, pageForm);
        return API.ok(detentionCenters);
    }


    @GetMapping("appointmentSettings")
    @PermissionDes(menu = "预约设置",name = "查看预约设置")
    @ApiOperation("获取预约设置 type 1-时间段设置 2-会见须知 3-时间段预约人数设置 4-温馨提示")
    public API<List<AppointmentSettings>> obtainAppointmentSettings(@RequestParam("type")Integer type,@RequestParam("detentionName")String detentionName){
        if(2 == type){
            detentionName="hjxz";
        }
        List<AppointmentSettings> appointmentSettings = accessService.obatinAppointmentSettings(type,detentionName);
        return API.ok(appointmentSettings);
    }

    @PostMapping("appointmentSettings")
    @PermissionDes(menu = "预约设置",name = "新增编辑预约设置")
    @ApiOperation("新增编辑预约设置")
    public API<String> insertAppointmentSettings(@RequestBody AppointmentSettings appointmentSettings){
        if(2 == appointmentSettings.getType()){
            appointmentSettings.setDetentionName("hjxz");
        }
        String string=appointmentSettings.getId()==null ? "添加":"编辑";
        accessService.insertAppointmentSettings(appointmentSettings);
        return API.ok(string);

    }

    @PostMapping("contactDetailsSort")
    @PermissionDes(menu = "预约设置",name = "联系方式排序")
    @ApiOperation("联系方式排序")
    public API<String> contactDetailsSort(@RequestBody List<AppointmentSettings> appointmentSettingses){
        accessService.contactDetailsSort(appointmentSettingses);
        return API.ok("排序成功");
    }


    @DeleteMapping("appointmentSettings")
    @PermissionDes(menu = "预约设置",name = "删除预约设置")
    @ApiOperation("删除预约设置")
    public API<String> deleteAppointmentSettings(String ids){
        accessService.deleteAppointmentSettings(ids);
        return API.ok("删除成功");
    }






}

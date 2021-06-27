package com.longpeng.jail.controller.manage;

import com.cq1080.auth.annotation.PermissionDes;
import com.cq1080.bean.form.PageForm;

import com.cq1080.meta.MetaUtils;
import com.cq1080.meta.bean.MetaData;
import com.cq1080.rest.API;


import com.longpeng.jail.bean.entity.Attorney;
import com.longpeng.jail.bean.request.InsertQualificationRequest;
import com.longpeng.jail.bean.request.ObtainQualificationRequest;
import com.longpeng.jail.bean.response.ObtainQualificationResponse;
import com.longpeng.jail.service.AttorneyService;
import com.longpeng.jail.util.pageHelper.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PermissionDes(menu = "律师管理")
@Api(tags = "律师管理")
@RequestMapping("manage/attorneyManagement/")
public class AttorneyManagementController {

    private AttorneyService attorneyService;

    @Autowired
    public AttorneyManagementController(AttorneyService attorneyService) {
        this.attorneyService = attorneyService;
    }

    @GetMapping("meta")
    @ApiOperation("配置元信息")
    public API<MetaData> userMeta() {
        return API.ok(MetaUtils.getMeta(Attorney.class));
    }

    @GetMapping("attorney")
    @PermissionDes(menu = "律师库",name = "查看律师列表")
    @ApiOperation("获取律师列表")
    public API<Page<Attorney>> getAttorneys(Attorney attorney, PageForm pageForm){
        Page<Attorney> attorneys = attorneyService.getAttorneys(attorney, pageForm);
        return API.ok(attorneys);
    }

    @PostMapping("attorney")
    @PermissionDes(menu = "律师库",name = "操作律师列表")
    @ApiOperation("操作律师列表")
    public API<String> insertAttorney(@RequestBody Attorney attorney){
        attorneyService.insertAttorney(attorney);
        return API.ok("成功");
    }

    @PostMapping("updateAttorney")
    @PermissionDes(menu = "律师库",name = "编辑律师信息")
    @ApiOperation("编辑律师信息")
    public API<String> updateAttorney(@RequestBody Attorney attorney){
        attorneyService.updateAttorney(attorney);
        return API.ok("成功");
    }

    @DeleteMapping("attorney")
    @PermissionDes(menu = "律师库",name = "删除律师列表")
    @ApiOperation("删除律师列表")
    public API<String> deleteAttorney(String ids){
        attorneyService.deleteAttorneys(ids);
        return API.ok("删除成功");
    }

    @GetMapping("exportAttorneyExcel")
    @PermissionDes(menu = "律师库",name = "导出律师库excel")
    @ApiOperation("导出律师库excel")
    public API<String> exportAttorneyExcel(){
        String excelUrl = attorneyService.exportAttorneyExcel();
        return API.ok(excelUrl);
    }

    @GetMapping("qualification")
    @PermissionDes(menu = "审核",name = "获取审核列表")
    @ApiOperation("获取审核列表")
    public API<PageResult<ObtainQualificationResponse>> obtainQualification(ObtainQualificationRequest obtainQualificationRequest){
        List<ObtainQualificationResponse> obtainQualificationResponses = attorneyService.obtainQualification(obtainQualificationRequest);
        return API.ok(PageResult.of(obtainQualificationResponses));
    }


    @PostMapping("qualification")
    @PermissionDes(menu = "审核",name = "审核律师资质")
    @ApiOperation("审核律师资质")
    public API<String> insertQualification(@RequestBody InsertQualificationRequest insertQualificationRequest){
        attorneyService.insertQualification(insertQualificationRequest);
        return API.ok("审核成功");
    }






}

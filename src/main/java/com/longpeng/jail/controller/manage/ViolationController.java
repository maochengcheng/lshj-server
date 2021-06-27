package com.longpeng.jail.controller.manage;

import com.cq1080.auth.annotation.PermissionDes;
import com.cq1080.bean.form.PageForm;
import com.cq1080.meta.MetaUtils;
import com.cq1080.meta.bean.MetaData;
import com.cq1080.rest.API;
import com.longpeng.jail.bean.entity.Access;
import com.longpeng.jail.bean.entity.ViolationLog;
import com.longpeng.jail.bean.request.AttorneyViolationListRequest;
import com.longpeng.jail.bean.request.ExportViolationLogExcelReq;
import com.longpeng.jail.bean.request.ObtainAccessViolationRequest;
import com.longpeng.jail.bean.request.UpdateViolationRequest;
import com.longpeng.jail.bean.response.AttorneyViolationListResponse;
import com.longpeng.jail.service.ViolationService;
import com.longpeng.jail.util.pageHelper.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("manage/violation/")
@PermissionDes(menu = "违约")
@Api(tags = "违约")
public class ViolationController {

    private ViolationService violationService;

    @Autowired
    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    @GetMapping("meta")
    @ApiOperation("配置元信息")
    public API<MetaData> userMeta() {
        return API.ok(MetaUtils.getMeta(ViolationLog.class));
    }

    @GetMapping("attorneyViolationListResponse")
    @PermissionDes(menu = "违规详细列表",name = "获取违约列表")
    @ApiOperation("获取违约列表")
    public API<PageResult<AttorneyViolationListResponse>> attorneyViolationListResponse(AttorneyViolationListRequest attorneyViolationListRequest){
        List<AttorneyViolationListResponse> attorneyViolationListResponses = violationService.attorneyViolationListResponses(attorneyViolationListRequest);
        return API.ok(PageResult.of(attorneyViolationListResponses));
    }

    @PostMapping("updateViolation")
    @PermissionDes(menu = "违规详细列表",name = "编辑违约次数")
    @ApiOperation("编辑违约次数")
    public API<String> updateViolation(@RequestBody UpdateViolationRequest updateViolationRequest){
        violationService.updateViolation(updateViolationRequest);
        return API.ok("修改成功");
    }

    @GetMapping("accessViolation")
    @PermissionDes(menu = "违规详细列表",name = "获取律师违约预约列表")
    @ApiOperation("获取律师违约预约列表")
    public API<PageResult<Access>> obtainAccessViolation(ObtainAccessViolationRequest obtainAccessViolationRequest){
        List<Access> accesses = violationService.obtainAccessViolation(obtainAccessViolationRequest);
        return API.ok(PageResult.of(accesses));
    }

    @PostMapping("accessViolation")
    @PermissionDes(menu = "违规详细列表",name = "消除系统违约记录")
    @ApiOperation("消除系统违约记录")
    public API<String> updateAccessViolation(@RequestParam("accessId") Integer accessId){
        violationService.updateAccessViolation(accessId);
        return API.ok("成功");
    }

    @GetMapping("violationLog")
    @PermissionDes(menu = "操作日志",name = "获取管理操作违约记录")
    @ApiOperation("获取管理操作违约记录")
    public API<Page<ViolationLog>> getAccess(ViolationLog violation, PageForm pageForm){
        Page<ViolationLog> violations = violationService.getViolationLog(violation, pageForm);
        return API.ok(violations);
    }

    @GetMapping("violationLogExcel")
    @PermissionDes(menu = "操作日志",name = "导出操作日志excel")
    @ApiOperation("导出操作日志excel")
    public API<String> exportViolationLogExcel(ExportViolationLogExcelReq exportViolationLogExcelReq){
        String exportExcel = violationService.exportViolationLogExcel(exportViolationLogExcelReq);
        return API.ok(exportExcel);
    }


}

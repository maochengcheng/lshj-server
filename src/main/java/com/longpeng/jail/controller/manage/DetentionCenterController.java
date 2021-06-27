package com.longpeng.jail.controller.manage;

import com.cq1080.auth.annotation.PermissionDes;
import com.cq1080.bean.form.PageForm;
import com.cq1080.meta.MetaUtils;
import com.cq1080.meta.bean.MetaData;
import com.cq1080.rest.API;
import com.longpeng.jail.bean.entity.DetentionCenter;
import com.longpeng.jail.bean.request.InsertDetentionCenterFreezeReq;
import com.longpeng.jail.service.DetentionCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@PermissionDes(menu = "单位管理")
@Api(tags = "单位管理")
@RequestMapping("manage/detentionCenter/")
public class DetentionCenterController {

    private DetentionCenterService detentionCenterService;

    @Autowired
    public DetentionCenterController(DetentionCenterService detentionCenterService) {
        this.detentionCenterService = detentionCenterService;
    }

    @GetMapping("meta")
    @ApiOperation("配置元信息")
    public API<MetaData> userMeta() {
        return API.ok(MetaUtils.getMeta(DetentionCenter.class));
    }

    @GetMapping("detentionCenter")
    @PermissionDes(menu = "单位库",name = "查看单位列表")
    @ApiOperation("获取单位列表")
    public API<Page<DetentionCenter>> getDetentionCenter(DetentionCenter detentionCenter, PageForm pageForm){
        Page<DetentionCenter> detentionCenters = detentionCenterService.getDetentionCenter(detentionCenter, pageForm);
        return API.ok(detentionCenters);
    }

    @PostMapping("detentionCenter")
    @PermissionDes(menu = "单位库",name = "操作单位列表")
    @ApiOperation("操作单位列表")
    public API<String> insertDetentionCenter(@RequestBody DetentionCenter detentionCenter){
        detentionCenterService.insertDetentionCenter(detentionCenter);
        return API.ok("成功");
    }

    @PostMapping("detentionCenterFreeze")
    @PermissionDes(menu = "单位库",name = "冻结解冻单位")
    @ApiOperation("冻结解冻单位")
    public API<String> insertDetentionCenterFreeze(@RequestBody InsertDetentionCenterFreezeReq insertDetentionCenterFreezeReq){
        detentionCenterService.insertDetentionCenterFreeze(insertDetentionCenterFreezeReq);
        return API.ok("成功");
    }

    @DeleteMapping("detentionCenter")
    @PermissionDes(menu = "单位库",name = "删除单位列表")
    @ApiOperation("删除单位列表")
    public API<String> deleteAttorney(String ids){
        detentionCenterService.deleteDetentionCenter(ids);
        return API.ok("删除成功");
    }
}

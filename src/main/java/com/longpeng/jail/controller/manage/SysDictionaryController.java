package com.longpeng.jail.controller.manage;

import com.cq1080.auth.annotation.PermissionDes;
import com.cq1080.bean.form.PageForm;
import com.cq1080.meta.MetaUtils;
import com.cq1080.meta.bean.MetaData;
import com.cq1080.rest.API;
import com.longpeng.jail.bean.entity.DetentionCenter;
import com.longpeng.jail.bean.entity.SysDictionary;

import com.longpeng.jail.service.SysDictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@PermissionDes(menu = "数据字典")
@Api(tags = "数据字典")
@RequestMapping("manage/sysDictionary/")
public class SysDictionaryController {
    private SysDictionaryService sysDictionaryService;

    @Autowired
    public SysDictionaryController(SysDictionaryService sysDictionaryService) {
        this.sysDictionaryService = sysDictionaryService;
    }

    @GetMapping("meta")
    @ApiOperation("配置元信息")
    public API<MetaData> userMeta() {
        return API.ok(MetaUtils.getMeta(DetentionCenter.class));
    }

    @GetMapping("getSysDictionary")
    @PermissionDes(menu = "字典库",name = "查看字典列表")
    @ApiOperation("获取字典列表")
    public API<Page<SysDictionary>> getSysDictionary(SysDictionary sysDictionary, PageForm pageForm){
        Page<SysDictionary> sysDictionarys = sysDictionaryService.getSysDictionary(sysDictionary, pageForm);
        return API.ok(sysDictionarys);
    }

    @GetMapping("getSysDictionaryItem")
    @PermissionDes(menu = "字典库",name = "查看字典项目列表")
    @ApiOperation("获取字典项目列表")
    public API<Page<SysDictionary>> getSysDictionaryItem(SysDictionary sysDictionary, PageForm pageForm){
        Page<SysDictionary> sysDictionarys = sysDictionaryService.getSysDictionaryItem(sysDictionary, pageForm);
        return API.ok(sysDictionarys);
    }

    @PostMapping("operateSysDictionary")
    @PermissionDes(menu = "字典库",name = "操作字典列表")
    @ApiOperation("操作字典列表")
    public API<String> operateSysDictionary(@RequestBody SysDictionary sysDictionary){
        sysDictionaryService.insertSysDictionary(sysDictionary);
        return API.ok("成功");
    }

    @PostMapping("operateSysDictionaryItem")
    @PermissionDes(menu = "字典库",name = "操作字典项目")
    @ApiOperation("操作字典项目")
    public API<String> operateSysDictionaryItem(@RequestBody SysDictionary sysDictionary){
        sysDictionaryService.insertSysDictionaryItem(sysDictionary);
        return API.ok("成功");
    }
    
    @DeleteMapping("deleteSysDictionary")
    @PermissionDes(menu = "字典库",name = "删除字典列表")
    @ApiOperation("删除字典列表")
    public API<String> deleteSysDictionary(String ids){
        sysDictionaryService.deleteSysDictionary(ids);
        return API.ok("删除成功");
    }
}

package com.cq1080.auth.controller;

import com.cq1080.auth.annotation.AnonUrl;
import com.cq1080.auth.annotation.PermissionDes;
import com.cq1080.auth.bean.*;
import com.cq1080.auth.service.PermissionService;
import com.cq1080.auth.service.AuthManagerService;
import com.cq1080.bean.form.PageForm;
import com.cq1080.meta.MetaUtils;
import com.cq1080.meta.bean.MetaData;
import com.cq1080.rest.API;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/manage/manager")
@Api(tags = "管理员")
@PermissionDes(menu = "用户管理")
public class AuthManagerAPI {

    private AuthManagerService userManagerService;

    private PermissionService permissionService;


    @Autowired
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Autowired
    public void setUserManagerService(AuthManagerService userManagerService) {
        this.userManagerService = userManagerService;
    }


    @AnonUrl
    @PostMapping("/login")
    @ApiOperation("管理员登录")
    public API<ManagerLoginVO> login(@RequestBody @Valid LoginForm loginForm){

        return API.ok(userManagerService.login(loginForm));
    }

    @AnonUrl
    @PostMapping("/init")
    @ApiOperation("初始化超级管理员")
    public API firstRegister(@RequestBody @Valid CreateManagerForm loginForm){
        userManagerService.createManager(loginForm,true);
        return API.ok();
    }

    @PostMapping("/pass")
    @ApiOperation("修改登录密码")
    public API<ManagerLoginVO>  changeManagerPassword(@RequestBody @Valid PasswordForm loginForm){
        userManagerService.changePassword(loginForm);
        return API.ok();
    }


    @ApiOperation("创建管理员")
    @PostMapping()
    @PermissionDes(menu = {"系统设置","设置管理员"},rewriteMenu = true,name = "创建管理员")
    public API<Manager>  createManager(@RequestBody @Valid CreateManagerForm createManagerForm){
        return API.ok(userManagerService.createManager(createManagerForm,false));
    }

    @ApiOperation("获取管理员列表")
    @GetMapping()
    @PermissionDes(menu = {"系统设置","设置管理员"},rewriteMenu = true,name = "管理员列表")
    public API<Page<Manager>>  getManager(Manager manager, PageForm pageForm){
        return API.ok(userManagerService.getManager(manager,pageForm));
    }


    @ApiOperation("删除管理员")
    @DeleteMapping()
    @PermissionDes(menu = {"系统设置","设置管理员"},rewriteMenu = true,name = "删除管理员")
    public API  deleteManager(@RequestParam String ids){
        userManagerService.deleteManager(ids);
        return API.ok();
    }


    @ApiOperation("获取管理员meta信息")
    @GetMapping("/meta")
    public API<MetaData> getManagerMeta(){
        return API.ok(MetaUtils.getMeta(Manager.class));
    }

    @ApiOperation("获取当前登录用户的权限")
    @GetMapping("/permission")
    public API<List<Permission>> getUserPermission(){

        return API.ok(permissionService.getSelfPermission());
    }




}

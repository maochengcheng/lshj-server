package com.longpeng.jail.controller.manage;

import com.cq1080.auth.annotation.PermissionDes;
import com.cq1080.rest.API;
import com.longpeng.jail.bean.entity.AppointmentSettings;
import com.longpeng.jail.bean.entity.Carousel;
import com.longpeng.jail.bean.response.DashboardResponse;
import com.longpeng.jail.service.AccessService;
import com.longpeng.jail.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("manage/home/")
@PermissionDes(menu = "首页")
@Api(tags = "首页")
public class HomeController {

    private StatisticsService statisticsService;
    private AccessService accessService;



    @Autowired
    public HomeController(StatisticsService statisticsService, AccessService accessService) {
        this.statisticsService = statisticsService;
        this.accessService = accessService;
    }

    @GetMapping("dashboard")
    @ApiOperation("看板")
    @PermissionDes(menu = "看板",name = "获取看板信息")
    public API<DashboardResponse> dashboard(){
        DashboardResponse dashboardResponse = statisticsService.dashboard();
        return API.ok(dashboardResponse);
    }

    @GetMapping("carousel")
    @ApiOperation("获取轮播图")
    @PermissionDes(menu = "轮播图",name = "获取轮播图")
    public API<List<Carousel>> getCarousels(){
        List<Carousel> carousels = statisticsService.getCarousels();
        return API.ok(carousels);
    }

    @PostMapping("carousel")
    @ApiOperation("添加编辑轮播图")
    @PermissionDes(menu = "轮播图",name = "添加编辑轮播图")
    public API<String> insertCarousel(@RequestBody Carousel carousel){
        statisticsService.insertCarousel(carousel);
        return API.ok("成功");
    }

    @DeleteMapping("carousel")
    @ApiOperation("删除轮播图")
    @PermissionDes(menu = "轮播图",name = "删除轮播图")
    public API<String> deleteCarousel(String ids){
        statisticsService.deleteCarousel(ids);
        return API.ok("删除成功");
    }

    @GetMapping("appointmentSettings")
    @ApiOperation("获取联系人电话 ")
    @PermissionDes(menu = "联系人设置",name = "获取联系人电话")
    public API<List<AppointmentSettings>> obtainAppointmentSettings(){
        String detentionName ="";
        List<AppointmentSettings> appointmentSettings = accessService.obatinAppointmentSettings(6,detentionName);
        return API.ok(appointmentSettings);
    }

    @PostMapping("appointmentSettings")
    @ApiOperation("添加编辑联系人电话")
    @PermissionDes(menu = "联系人设置",name = "添加编辑联系人电话")
    public API<String> insertAppointmentSettings(@RequestBody AppointmentSettings appointmentSettings){
        accessService.insertAppointmentSettings(appointmentSettings);
        return API.ok("成功");
    }

    @DeleteMapping("appointmentSettings")
    @PermissionDes(menu = "联系人设置",name = "删除联系人电话")
    @ApiOperation("删除联系人电话")
    public API<String> deleteAppointmentSettings(String ids){
        accessService.deleteAppointmentSettings(ids);
        return API.ok("删除成功");
    }

}

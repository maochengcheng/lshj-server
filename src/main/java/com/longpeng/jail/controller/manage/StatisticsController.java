package com.longpeng.jail.controller.manage;


import com.cq1080.auth.annotation.PermissionDes;
import com.cq1080.rest.API;
import com.longpeng.jail.bean.response.StatisticsResponse;
import com.longpeng.jail.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PermissionDes(menu = "数据统计")
@Api(tags = "数据统计")
@RequestMapping("manage/statistics/")
public class StatisticsController {

    private StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("accessMeetNumber")
    @PermissionDes(menu = "预约会见统计",name = "获取每日会见数图表数据")
    @ApiOperation("获取每日会见数图表数据")
    public API<List<StatisticsResponse>> accessMeetNumber(@RequestParam("start")String start,@RequestParam("end")String end){
        List<StatisticsResponse> statisticsResponses = statisticsService.getStatisticsResponse(1, start, end);
        return API.ok(statisticsResponses);
    }

    @GetMapping("accessNumber")
    @PermissionDes(menu = "预约会见统计",name = "获取每日预约数图表统计")
    @ApiOperation("获取每日预约数图表统计")
    public API<List<StatisticsResponse>> accessNumber(@RequestParam("start")String start,@RequestParam("end")String end){
        List<StatisticsResponse> statisticsResponses = statisticsService.getStatisticsResponse(2, start, end);
        return API.ok(statisticsResponses);
    }
}

package com.longpeng.jail.service;

import com.cq1080.utils.StringUtil;
import com.github.wenhao.jpa.Specifications;
import com.longpeng.jail.bean.entity.Carousel;
import com.longpeng.jail.bean.response.DashboardResponse;
import com.longpeng.jail.bean.response.StatisticsResponse;
import com.longpeng.jail.datasource.mapper.AccessMapper;
import com.longpeng.jail.datasource.mapper.AttorneyMapper;
import com.longpeng.jail.datasource.mapper.CarouselMapper;
import com.longpeng.jail.datasource.repository.CarouselRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StatisticsService {

    private AttorneyMapper attorneyMapper;
    private AccessMapper accessMapper;
    private CarouselRepository carouselRepository;
    private CarouselMapper carouselMapper;

    @Autowired
    public StatisticsService(AttorneyMapper attorneyMapper, AccessMapper accessMapper, CarouselRepository carouselRepository, CarouselMapper carouselMapper) {
        this.attorneyMapper = attorneyMapper;
        this.accessMapper = accessMapper;
        this.carouselRepository = carouselRepository;
        this.carouselMapper = carouselMapper;
    }

    /**
     * 首页看板
     * @return
     */
    public DashboardResponse dashboard(){
        Date d = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        String start = sdf1.format(d);
        String end = sdf2.format(d);
        DashboardResponse dashboardResponse=new DashboardResponse();
        dashboardResponse.setAttorneyNumber(attorneyMapper.attorneyTotalNumber());
        dashboardResponse.setAccessTotalNumber(accessMapper.accessTotalNumber());
        dashboardResponse.setAccessTodayTotalNumber(accessMapper.accessNumberByAt(Timestamp.valueOf(start),Timestamp.valueOf(end)));
        dashboardResponse.setAccessMeetTodayNumber(accessMapper.accessMeetNumberByAt(Timestamp.valueOf(start),Timestamp.valueOf(end)));
        return dashboardResponse;
    }

    /**
     * 获取轮播图列表
     * @return
     */
    public List<Carousel> getCarousels(){
        List<Carousel> presence_status = carouselRepository.findAll(Specifications.<Carousel>and().eq("presenceStatus", 1).build(), Sort.by(Sort.Order.by("serialNumber")));
        return presence_status;
    }

    /**
     * 添加编辑轮播图
     * @param carousel
     * @return
     */
    public Carousel insertCarousel(Carousel carousel){
        carouselRepository.save(carousel);
        return carousel;
    }

    /**
     * 删除轮播图
     * @param ids
     */
    public void deleteCarousel(String ids){
        Integer[] integers = StringUtil.toIntArray(ids);
        for(Integer id:integers){
            Carousel carousel = carouselRepository.findOne(Specifications.<Carousel>and().eq("id", id).build()).orElse(null);
            carouselRepository.deleteById(id);
            if(carousel!=null){
                carouselMapper.updateSerialNumber(carousel.getSerialNumber());
            }
        }
    }

    /**
     *
     * @param type
     * @param start
     * @param end
     * @return
     */
    public List<StatisticsResponse> getStatisticsResponse(Integer type,String start,String end){
        List<StatisticsResponse> statisticsResponses=new ArrayList<>();
        if(type.equals(1)){
            statisticsResponses=accessMapper.getAccessMeetNumber(start,end);
        }
        if(type.equals(2)){
            statisticsResponses=accessMapper.getAccessNumber(start,end);
        }
        return statisticsResponses;
    }
}

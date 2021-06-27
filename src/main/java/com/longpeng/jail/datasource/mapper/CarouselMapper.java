package com.longpeng.jail.datasource.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CarouselMapper {
    @Update("update carousel set serial_number=serial_number-1 where serial_number>#{serialNumber}")
    int updateSerialNumber(Integer serialNumber);
}

package com.longpeng.jail.datasource.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface DetentionCenterMapper {

    @Update("update detention_center set presence_status=0 where id=#{id}")
    int deleteDetentionCenterById(Integer id);
}

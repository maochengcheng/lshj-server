package com.longpeng.jail.datasource.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface AppointmentSettingsMapper {
    @Delete("delete from appointment_settings where type=#{type} and period=#{period}")
    int deleteByTypePeriod(Integer type,String period);

    @Update("update appointment_settings set serial_number=serial_number-1 where type=6 and serial_number>#{serialNumber}")
    int deleteContactDetails(Integer serialNumber);

    @Select("select IFNULL(sum(people_number),0) from appointment_settings where type=3 and serial_number=#{week} and presence_status=1")
    Integer weekTotalPeopleNumber(int week);
}

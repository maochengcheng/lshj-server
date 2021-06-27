package com.longpeng.jail.datasource.mapper;

import com.longpeng.jail.bean.entity.Access;
import com.longpeng.jail.bean.entity.Certification;
import com.longpeng.jail.bean.excel.AccessExcel;
import com.longpeng.jail.bean.response.StatisticsResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
@Mapper
public interface AccessMapper {
    @Update("update access set presence_status=0 where id=#{id}")
    int deleteAccessById(Integer id);

    @Select("select * from access where presence_status=1 and access_status in ('NOTVISITED','VIOLATION') and attorney_id=#{attorneyId} and certification='PASS' and YEAR(appointment_time)=YEAR(NOW()) and appointment_time<now()")
    List<Access> obtainAccessViolation(Integer attorneyId);

    @Select("select IFNULL(count(id),0) from access where presence_status=1 ")
    Integer accessTotalNumber();

    @Select("select IFNULL(count(id),0) from access where appointment_time>=#{start} and appointment_time<=#{end} and presence_status=1 and certification='PASS'")
    Integer accessNumberByAt(Timestamp start,Timestamp end);

    @Select("select IFNULL(count(id),0) from access where appointment_time>=#{start} and appointment_time<=#{end} and presence_status=1 and access_status in ('VISITED','END')")
    Integer accessMeetNumberByAt(Timestamp start,Timestamp end);

    @Select("select * from access where attorney_id=#{attorneyId} and type=#{type} and certification=#{certification} order by create_time desc")
    List<Access> getAccessListByAidTypeAt(Integer attorneyId, Access.Type type, Certification certification);

    @Select("select * from access where attorney_id=#{attorneyId} and type=#{type} order by create_time desc")
    List<Access> getAccessListByAidType(Integer attorneyId, Access.Type type);

    @Select("select DATE_FORMAT(appointment_time,'%Y-%m-%d') date,count(appointment_time) number from access where appointment_time>=#{start} and appointment_time<=#{end} and access_status in ('VISITED','END') and presence_status=1 group by date order by date")
    List<StatisticsResponse> getAccessMeetNumber(String start,String end);

    @Select("select DATE_FORMAT(appointment_time,'%Y-%m-%d') date,count(appointment_time) number from access where appointment_time>=#{start} and appointment_time<=#{end} and presence_status=1 group by date order by date")
    List<StatisticsResponse> getAccessNumber(String start,String end);

    @Select("select IFNULL(count(id),0) from access where attorney_id=#{attorneyId}")
    Integer getAccessNumberByAid(Integer attorneyId);

    @Select("select IFNULL(count(id),0) from access where presence_status=1 and certification='PASS' and access_status in ('NOTVISITED','VIOLATION') and attorney_id=#{attorneyId} and YEAR(appointment_time)=YEAR(NOW()) and appointment_time<now()")
    Integer getViolationNumberByAid(Integer attorneyId);

    @Select("select appointment_time from access where attorney_id=#{attorneyId} and certification='PASS' and access_status in ('NOTVISITED','VIOLATION') order by appointment_time desc limit 0,1")
    Timestamp getAppointmentTime(Integer attorneyId);

    @Select("select IFNUll(count(id),0) from access  where detention_center_name = #{detentionName} and appointment_time>=#{start} and appointment_time<=#{end} and certification!='REJECTED' and certification!='CANCEL' and presence_status=1 ")
    Integer getAccessNumberCount(String start,String end,String detentionName);

    @Select("select IFNUll(count(id),0) from access  where appointment_time>=#{start} and appointment_time<=#{end} and certification!='REJECTED' and type=#{type} and presence_status=1 ")
    Integer getAccessNumberCountByType(String start,String end,Access.Type type);

    @Select("select * from access where appointment_time>=#{start} and appointment_time<=#{end} and presence_status=1")
    List<AccessExcel> exportAccessExcel(String start,String end);

    @Select("select * from access where appointment_time>=#{start} and appointment_time<=#{end} and presence_status=1 and detention_center_id=#{detentionCenterId}")
    List<AccessExcel> exportAccessExcel2(String start,String end,Integer detentionCenterId);

    @Update("update access set certification=#{certification} where id=#{accessId}")
    int cancelAccess(Certification certification,Integer accessId);



}

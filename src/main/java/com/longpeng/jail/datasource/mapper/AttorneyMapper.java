package com.longpeng.jail.datasource.mapper;

import com.longpeng.jail.bean.excel.AttorneyExcel;
import com.longpeng.jail.bean.request.AttorneyViolationListRequest;
import com.longpeng.jail.bean.request.ObtainQualificationRequest;
import com.longpeng.jail.bean.response.AttorneyViolationListResponse;
import com.longpeng.jail.bean.response.ObtainQualificationResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Mapper
@Component
public interface AttorneyMapper {

    @Update("update attorney set presence_status=0 where id=#{id}")
    int deleteAttorneyById(Integer id);

    @Update("update qualification set presence_status=0 where attorney_id=#{attorneyId}")
    int updateQualificationByAid(Integer attorneyId);

    @Select("select IFNULL(count(id),0) from attorney where presence_status=1 and name is not null")
    Integer attorneyTotalNumber();

    @Select("select * from attorney where presence_status=1 and name is not null")
    List<AttorneyExcel> exportAttorneyExcel();

    @SelectProvider(type = attorneySqlProvider.class,method = "obtainQualification")
    List<ObtainQualificationResponse> getObtainQualificationResponses(ObtainQualificationRequest obtainQualificationRequest);

    @SelectProvider(type = attorneySqlProvider.class,method = "attorneyViolationListResponses")
    List<AttorneyViolationListResponse> getAttorneyViolationListResponses(AttorneyViolationListRequest attorneyViolationListRequest);

    class attorneySqlProvider{
        public String obtainQualification(ObtainQualificationRequest obtainQualificationRequest){
            return new SQL(){{
                SELECT("id attorney_id,face_img,wechat_avatar,name,gender,phone,id_number,license_number,certification,create_time,law_office");
                FROM("attorney");
                if(!StringUtils.isEmpty(obtainQualificationRequest.getStart())&& !StringUtils.isEmpty(obtainQualificationRequest.getEnd())){
                    WHERE("update_time>=#{start} and update_time<=#{end}");
                }
                if(!StringUtils.isEmpty(obtainQualificationRequest.getName())){
                    WHERE("name like concat('%',#{name},'%') ");
                }
                if(!StringUtils.isEmpty(obtainQualificationRequest.getCertification())){
                    WHERE("certification=#{certification}");
                }
                WHERE("name is not  null");
                ORDER_BY("create_time desc");
            }}.toString();
        }
        public String attorneyViolationListResponses(AttorneyViolationListRequest attorneyViolationListRequest){
            return new SQL(){{
                SELECT("id attorney_id,name attorney_name,id_number,artificial_violation,license_number");
                FROM("attorney");
                if(!StringUtils.isEmpty(attorneyViolationListRequest.getAttorneyName())){
                    WHERE("name like concat('%',#{attorneyName},'%')");
                }
                WHERE("presence_status=1 and name is not null");
            }}.toString();
        }
    }
}

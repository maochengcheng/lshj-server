package com.longpeng.jail.datasource.mapper;

import com.longpeng.jail.bean.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface VerificationCodeMapper {

    @Select("select * from verification_code where phone=#{phone} order by create_time desc limit 0,1")
    VerificationCode getVerificationCode(String phone);
}

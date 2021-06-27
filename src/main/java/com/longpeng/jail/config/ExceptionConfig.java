package com.longpeng.jail.config;






import com.cq1080.rest.API;
import com.cq1080.rest.APIException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionConfig {

    @ExceptionHandler(APIException.class)
    @ResponseBody
    public API handle(APIException e){
        return API.e(e.getApiError().getCode(),e.getApiError().getMsg());
    }


//    @ExceptionHandler(Exception.class)
//    public API handleException(Exception e) {
//        return API.e(500, "系统繁忙,请稍后再试");
//    }
}

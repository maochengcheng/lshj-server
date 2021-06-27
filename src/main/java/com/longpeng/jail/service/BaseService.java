package com.longpeng.jail.service;

import com.cq1080.auth.bean.Manager;
import com.cq1080.auth.bean.ManagerLoginLog;
import com.cq1080.auth.repository.ManagerLoginLogRepository;
import com.cq1080.auth.repository.ManagerRepository;
import com.cq1080.rest.APIError;
import com.github.wenhao.jpa.Specifications;
import com.longpeng.jail.bean.entity.Attorney;
import com.longpeng.jail.datasource.repository.AttorneyRepository;
import com.longpeng.jail.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class BaseService {

    private ApplicationContext mContext;

    @Autowired
    RedisCache redisCache;

    @Autowired
    AttorneyRepository attorneyRepository;

    @Autowired
    ManagerLoginLogRepository managerLoginLogRepository;

    @Autowired
    ManagerRepository managerRepository;

    public Attorney getCurrentUser(){
        String token = (String) getHeader("token");
        return redisCache.getCacheObject(token);
    }

    public Manager getManager(){
        String accessToken = (String) getHeader("accessToken");
        ManagerLoginLog managerLoginLog = managerLoginLogRepository.findOne(Specifications.<ManagerLoginLog>and().eq("token", accessToken).build()).orElse(null);
        if(managerLoginLog==null){
            APIError.NEED_LOGIN();
        }
        Manager manager = managerRepository.findOne(Specifications.<Manager>and().eq("id", managerLoginLog.getManagerId()).build()).orElse(null);
        if(manager==null){
            APIError.e(401,"登陆用户已经不存在");
        }
        return manager;
    }

    public  Object  getHeader(String key){
        return getCurrentRequest().getHeader(key);
    }

    protected HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        return servletRequest;
    }

    public void sendEvent(Object o){
        mContext.publishEvent(o);
    }
}

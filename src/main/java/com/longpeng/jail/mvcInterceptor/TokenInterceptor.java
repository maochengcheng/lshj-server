package com.longpeng.jail.mvcInterceptor;




import com.cq1080.rest.APIError;
import com.longpeng.jail.redis.RedisCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class TokenInterceptor implements HandlerInterceptor {

    @Resource
    private RedisCache redisServer;

    @Value("${spring.redis.timeout}")
    long expireTokenTime;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            APIError.NEED_LOGIN();
        }

        Object o = redisServer.getCacheObject(token);
        if (o != null) {
            redisServer.expier(token, expireTokenTime);
        } else {
            APIError.NEED_LOGIN();
        }
        return true;
    }


}

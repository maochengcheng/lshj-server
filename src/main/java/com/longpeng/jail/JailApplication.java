package com.longpeng.jail;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.context.request.RequestContextListener;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cq1080.auth","com.longpeng.jail"})
@EntityScan(basePackages = {"com.cq1080.auth","com.longpeng.jail"})
@EnableJpaRepositories(basePackages = {"com.cq1080.auth","com.longpeng.jail"})
/**
 * @Author longpeng
 * 正式交付源码 2020-03-11
 * 二次交付源码日期 2020-05-05
 */
public class JailApplication {



    public static void main(String[] args) {
        SpringApplication.run(JailApplication.class, args);
    }

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }




}

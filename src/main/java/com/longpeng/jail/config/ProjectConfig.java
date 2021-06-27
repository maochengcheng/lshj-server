package com.longpeng.jail.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "project")
public class ProjectConfig {
    //上传文件路径
    private String uploadPath;

    //项目后台域名
    private String url;

}

package com.longpeng.jail.config;

import com.cq1080.auth.config.AuthResourceConfig;
import com.cq1080.auth.repository.PermissionRepository;
import com.longpeng.jail.mvcInterceptor.CommonInterceptor;
import com.longpeng.jail.mvcInterceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.io.File;


@Configuration
public class WebMvcConfig extends AuthResourceConfig {
    public WebMvcConfig(PermissionRepository permissionRepository) {
        super(permissionRepository);
    }

    @Bean
    public CommonInterceptor commonInterceptor() {
        return new CommonInterceptor();
    }

    @Bean
    public TokenInterceptor tokenInterceptor() {
        return new TokenInterceptor();
    }

    private ProjectConfig projectConfig;
    @Autowired
    public void setProjectConfig(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }


    @Override
    /**
     * 拦截请求
     */
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(commonInterceptor()).addPathPatterns("/**").excludePathPatterns("/doc.html");
        registry.addInterceptor(tokenInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/login");
    }

    /**
     * 静态资源配置
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry){
        super.addResourceHandlers(registry);

        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:"+projectConfig.getUploadPath()+ File.separator+"images"+ File.separator);

        registry.addResourceHandler("/excel/**")
                .addResourceLocations("file:"+projectConfig.getUploadPath()+ File.separator+"excel"+ File.separator);

        registry.addResourceHandler("/**")
                .addResourceLocations("file:"+projectConfig.getUploadPath()+File.separator+"jailpc"+File.separator);
    }

    /**
     * 首页配置
     */
    @Bean
    public ITemplateResolver templateResolver(){
        return getResolver(projectConfig.getUploadPath()+File.separator+"jailpc"+ File.separator);
    }

    private FileTemplateResolver getResolver(String path){
        FileTemplateResolver resolver =new FileTemplateResolver();
        resolver.setPrefix(path);
        resolver.setSuffix(".html");
        resolver.setOrder(1);
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }



}

package com.outdoor.config;

import com.outdoor.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.url-prefix}")
    private String urlPrefix;

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 统一使用项目根目录 + upload.path
        String projectPath = System.getProperty("user.dir");
        String absoluteUploadPath = projectPath + "/" + uploadPath;

        registry.addResourceHandler(urlPrefix + "**")
                // 先从文件系统读取（上传的新文件，src/main/resources/static/images/**）
                .addResourceLocations("file:" + absoluteUploadPath,
                        // 再回退到 classpath 下的静态资源（项目自带的图标等）
                        "classpath:/static/images/");

        // 静态资源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // 兼容旧的图片路径（/images/**）
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 前台公开页面
                        "/",
                        "/index",
                        "/login",
                        "/register",
                        "/activities",
                        "/activity/**",
                        "/moments",
                        "/notices",
                        "/notice/**",
                        "/profile/**",
                        // 公开API
                        "/api/login",
                        "/api/register",
                        "/api/logout",
                        "/api/current-user",
                        "/api/front/**",
                        // 静态资源
                        "/static/**",
                        "/uploads/**",
                        "/images/**",
                        "/error"
                );
    }
}

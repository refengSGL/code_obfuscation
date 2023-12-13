package com.refengSGL.config;

import com.refengSGL.common.JwtInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@AllArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    /**  注册拦截器并配置规则  */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 以下的是放行的页面
        registry.addInterceptor(jwtInterceptor)
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("/test/**")
                .excludePathPatterns("/api/file/download/**");

    }
}

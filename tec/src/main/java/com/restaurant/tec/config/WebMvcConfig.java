package com.restaurant.tec.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ActivityInterceptor activityInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // Interceptar todas las peticiones para registrar actividad
        registry.addInterceptor(activityInterceptor).addPathPatterns("/**");
    }
}

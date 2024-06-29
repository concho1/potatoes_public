package com.goott.potatoes.manager.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private Interceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**")
                .excludePathPatterns(
                        "/**/*.js", "/**/*.css", "/**/*.png",
                        "/**/*.jpg", "/**/*.jpeg", "/**/*.gif",
                        "/**/*.bmp", "/**/*.ico", "/**/*.svg",
                        "/**/*.webp", "/**/*.woff", "/**/*.woff2",
                        "/**/*.ttf", "/**/*.eot");
    }
}

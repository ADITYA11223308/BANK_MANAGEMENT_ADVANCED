package com.bm.webapp.config;

import com.bm.webapp.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns(
                        "/dashboard/**",
                        "/account/**",
                        "/deposit/**",
                        "/withdraw/**",
                        "/transfer/**",
                        "/transactions/**",
                        "/profile/**"
                );
    }
}

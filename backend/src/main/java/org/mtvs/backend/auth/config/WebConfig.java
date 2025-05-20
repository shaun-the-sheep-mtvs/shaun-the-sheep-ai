package org.mtvs.backend.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3002")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // OPTIONS 추가
                .allowedHeaders("*")                                       // 요청 헤더 전부 허용
                .allowCredentials(true)                                    // 쿠키 같은 인증 정보 허용
                .maxAge(3600);                                             // preflight 캐시 1시간
    }
}
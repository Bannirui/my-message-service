package com.github.bannirui.mms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许任意域
        config.addAllowedOriginPattern("*");
        // 允许任意请求头
        config.addAllowedHeader("*");
        // 允许任意方法（GET、POST、PUT、DELETE、OPTIONS）
        config.addAllowedMethod("*");
        // 允许携带cookie
        config.setAllowCredentials(true);
        // 预检请求缓存时间
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

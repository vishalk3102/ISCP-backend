package com.iscp.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    private String[] allowedOrigins;

    @Value("${cors.allow-credentials}")
    private Boolean allowCredentials;

    @Value("${cors.allowed-headers}")
    private String[] allowedHeaders;

    @Value("${cors.allowed-methods}")
    private String[] allowedMethods;

    public CorsConfig(String[] allowedOrigins, @Value("${cors.allow-credentials}") Boolean allowCredentials, @Value("${cors.allowed-headers}") String[] allowedHeaders, @Value("${cors.allowed-methods}") String[] allowedMethods) {
        this.allowedOrigins = allowedOrigins;
        this.allowCredentials = allowCredentials;
        this.allowedHeaders = allowedHeaders;
        this.allowedMethods = allowedMethods;
    }

    // Define a Bean for CorsConfigurationSource to manage CORS policy
    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfigurationConfig=new CorsConfiguration();
//        corsConfigurationConfig.setAllowedOrigins(List.of("http://192.168.2.25:4200"));
//        corsConfigurationConfig.setAllowedOrigins(List.of("http://172.16.1.65:4200"));
        corsConfigurationConfig.setAllowedOrigins(List.of("http://localhost:4200"));
//        corsConfigurationConfig.setAllowedOrigins(List.of("http://iscpqa.contata.co.in"));
        corsConfigurationConfig.setAllowedMethods(Arrays.asList(allowedMethods));
        corsConfigurationConfig.setAllowedHeaders(Arrays.asList(allowedHeaders));
        corsConfigurationConfig.setAllowCredentials(allowCredentials);
        source.registerCorsConfiguration("/**",corsConfigurationConfig);
        return  source;
    }
}

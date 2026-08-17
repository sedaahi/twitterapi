package com.workintech.twitterapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    private final String allowedOrigin;

    public CorsConfig(
            @Value("${app.cors.allowed-origin}") String allowedOrigin
    ) {
        this.allowedOrigin = allowedOrigin;
    }

    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration configuration = new CorsConfiguration();

        // React uygulamamızın backend'e request atmasına izin veriyoruz.
        configuration.setAllowedOrigins(
                List.of(allowedOrigin)
        );

        // Frontend'in kullanabileceği HTTP metodları
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        // Authorization header dahil request headerlarına izin veriyoruz.
        configuration.setAllowedHeaders(
                List.of("*")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        // Bu CORS kuralları bütün endpointler için geçerli.
        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return new CorsFilter(source);
    }
}

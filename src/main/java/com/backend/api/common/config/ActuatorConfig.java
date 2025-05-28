package com.backend.api.common.config;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

@Configuration
public class ActuatorConfig {

    // actuator CORS 설정을 위한 WebMvcConfigurer
    @Bean
    public WebMvcConfigurer actuatorCorsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/actuator/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    // actuator 링크 생성 (옵션)
    @Bean
    public EndpointLinksResolver endpointLinksResolver(WebEndpointsSupplier supplier,
                                                       WebEndpointProperties webEndpointProperties,
                                                       Environment environment) {
        return new EndpointLinksResolver(
                supplier.getEndpoints(),
                webEndpointProperties.getBasePath()
        );
    }
}

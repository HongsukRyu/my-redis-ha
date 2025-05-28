package com.backend.api.common.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@SecurityScheme(
//        name = "Authorization",
//        type = SecuritySchemeType.HTTP,
//        scheme = "bearer",
//        bearerFormat = "JWT"
//)
@SecurityRequirement(name = "Authorization")
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Backend API")
                        .description("백엔드 API 명세서")
                        .version("v1.0"))
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("Authorization"));
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("backend-api")
                .packagesToScan("com.backend.api.controller") // controller 패키지 지정
                .build();
    }
}
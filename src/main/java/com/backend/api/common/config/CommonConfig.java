package com.backend.api.common.config;

import com.backend.api.common.handler.CustomInterceptor;

import org.apache.catalina.Container;
import org.apache.catalina.core.StandardHost;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ALL")
@Configuration
@EnableWebMvc
@EnableScheduling
@EnableTransactionManagement
public class CommonConfig implements WebMvcConfigurer {

    Environment env;

    @Autowired
    public CommonConfig(Environment env) {
        this.env = env;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));
    }

    /**
     * Content-Type 기본 인코딩 변경 ( ISO-8859-1 -> UTF-8)
     *
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(converter -> converter instanceof StringHttpMessageConverter)
                .forEach(converter -> ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8));
    }

    /**
     * spring boot view resource config set
     *
     */
    @Bean
    public ViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setSuffix(".html");
        resolver.setCache(false);
        return resolver;
    }

    /**
     * Tomcat 기본 에러 페이지 커스텀
     * URL에 허용되지 않는 특수문자가 포함될 경우 에러처리 대응
     * CustomErrorReportValveHandler 에서 에러 내용 출력
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> errorReportValveCustomizer() {
        return (factory) -> factory.addContextCustomizers(context -> {
            final Container parent = context.getParent();
            if (parent instanceof StandardHost) {
                ((StandardHost) parent).setErrorReportValveClass(
                        "handler.common.com.backend.api.CustomErrorReportValveHandler");
            }
        });
    }

    /**
     * CORS controller config set
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods(HttpMethod.POST.name(), HttpMethod.GET.name(), HttpMethod.OPTIONS.name())
                .allowedHeaders("Authorization", "AccessToken", "RefreshToken", "userId", "classId", "initialPw", "status", "email", "userType",
                        "count", "Accept", "Content-Type", "Origin", "XSRF-TOKEN", "X-XSRF-TOKEN", "X-Requested-With",
                        "referrer", "Access-Control-Allow-Headers", "Cookie")
                .exposedHeaders("Authorization", "AccessToken", "Content-Disposition", "RefreshToken", "userId", "classId", "initialPw", "status", "email", "userType",
                        "count", "Accept", "Content-Type", "Origin", "XSRF-TOKEN", "X-XSRF-TOKEN", "X-Requested-With",
                        "referrer", "Access-Control-Allow-Headers", "Set-Cookies")
                // CORS - addCorsMappings updated
                .allowedOriginPatterns("*");
    }

    /**
     * JPA model mapper
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setPropertyCondition(Conditions.isNotNull())
                .setSourceNamingConvention(NamingConventions.NONE)
                .setDestinationNamingConvention(NamingConventions.NONE)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CustomInterceptor()).addPathPatterns("/api/**");
    }
}
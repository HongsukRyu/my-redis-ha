package com.backend.api.common.config;

import com.backend.api.common.handler.AuthenticationFilter;
import com.backend.api.common.handler.AuthorizationFilter;
import com.backend.api.common.handler.CustomLogoutSuccessHandler;
import com.backend.api.common.handler.RequestAuthorizationFilter;
import com.backend.api.service.aclpolicy.IAclPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


import com.backend.api.common.utils.AuthenticationUtils;
import com.backend.api.common.utils.Utils;
import com.backend.api.service.history.ILoginHistoryService;
import com.backend.api.service.history.ILoginTryHistoryService;
import com.backend.api.service.user.IUserService;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final IUserService userService;
    private final Environment env;
    private final ILoginHistoryService loginHistoryService;
    private final ILoginTryHistoryService loginTryHistoryService;
    private final AuthenticationUtils utils;
    private final Utils commonUtils;
    private final IAclPolicyService aclPolicyService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        // 람다 스타일 DSL 로 변경
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(
                                "/api/users/login",
                                "/api/users/logout",
                                "/error/**",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler(userService, env))
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // custom entrypoint handling
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterAt(new AuthenticationFilter(authenticationManager, env, userService, utils, loginHistoryService, loginTryHistoryService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new AuthorizationFilter(authenticationManager, env, userService), BasicAuthenticationFilter.class)
                .addFilterAfter(new RequestAuthorizationFilter(authenticationManager, userService, aclPolicyService, env, commonUtils), AuthorizationFilter.class);

        return http.build(); // 이제 안전
    }

    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        List<String> allowHeaderList = Arrays.asList(
                "AccessToken", "RefreshToken", "Content-Type", "x-xsrf-token", "Content-Disposition",
                "Origin", "Accept", "X-Requested-With", "Access-Control-Max-Age",
                "Access-Control-Allow-Methods", "Access-Control-Allow-Headers",
                "Access-Control-Request-Method", "Access-Control-Request-Headers",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials",
                "x-forwarded-for", "x-forwarded-proto", "x-forwarded-host",
                "x-real-ip", "x-forwarded-port",
                "Access-Control-Expose-Headers"
        );
        config.setExposedHeaders(allowHeaderList);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Web Security Customizer (정적 자원 등 무시할 경로)
     */
    @Bean
    @Profile({"!dev"})
    public WebSecurityCustomizer webSecurityCustomizerDev() {
        return web -> web.ignoring().requestMatchers(
                "/v3/api-docs", "/configuration/**",
                "/resources/**", "/dist/**", "/css/**", "/font-awesome/**",
                "/fonts/**", "/favicon.ico", "/img/**", "/js/**", "/pdf/**",
                "/api/account/refreshAccessToken",
                "/webjars/**",
                "/swagger*/**",
                "/api-docs/**",
                "/monitor/prometheus/**"
        );
    }

    @Bean
    @Profile({"dev"})
    public WebSecurityCustomizer webSecurityCustomizerProd() {
        return web -> web.ignoring().requestMatchers(
                "/v3/api-docs", "/configuration/**",
                "/resources/**", "/dist/**", "/css/**", "/font-awesome/**",
                "/fonts/**", "/favicon.ico", "/img/**", "/js/**", "/pdf/**",
                "/api/account/refreshAccessToken",
                "/webjars/**",
                "/monitor/prometheus/**"
        );
    }
}
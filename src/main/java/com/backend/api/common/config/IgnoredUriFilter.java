package com.backend.api.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@Order(1)
public class IgnoredUriFilter extends OncePerRequestFilter {

    private static final Set<String> IGNORED_URIS = Set.of(
            "/favicon.ico", "/robots.txt", "/manifest.json", "/fonts/ftnt-icons.woff"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (IGNORED_URIS.contains(request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
            return;
        }

        filterChain.doFilter(request, response);
    }
}

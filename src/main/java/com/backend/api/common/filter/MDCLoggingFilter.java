package com.backend.api.common.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;

import jakarta.servlet.*;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCLoggingFilter implements Filter {

    // MDC (Mapped Diagnostic Context) : 현재 실행중인 쓰레드에 메타 정보를 넣고 관리하는 공간
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // uuid
        final UUID uuid = UUID.randomUUID();
        // request 별 uuid 를 할당해서 MDC 에 넣음
        MDC.put("request_id", uuid.toString());
        filterChain.doFilter(servletRequest, servletResponse);
        MDC.clear();
    }
}

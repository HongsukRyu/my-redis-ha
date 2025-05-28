package com.backend.api.common.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

// Spring Framework v5.0 부터는 HandlerInterceptor interface 사용
// HandlerInterceptorAdapter -> HandlerInterceptor

@Slf4j
public class CustomInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // This method is called before the actual handler is executed.
        // You can perform pre-processing here.
        return true; // Return true to proceed with the execution chain.
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        // This method is called after the handler is executed but before the view is rendered.
        // You can perform post-processing here.
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex != null) {
            log.error("Request [{} {}] 에러 발생", request.getMethod(), request.getRequestURI(), ex);
        }
    }
}


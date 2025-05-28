package com.backend.api.common.handler;

import com.backend.api.service.user.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final IUserService userService;
    private final Environment env;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            String authorizationHeader = request.getHeader(env.getProperty("authorization.token.header.access-name"));
            String token = authorizationHeader
                    .replace(Objects.requireNonNull(env.getProperty("authorization.token.header.prefix")), "")
                    .replaceAll(" ", "");

            Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(Objects.requireNonNull(env.getProperty("token.secret")).getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token.trim())
                    .getBody();
            String userId = (String) claims.get("userId");
            userService.deleteToken(userId);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }
}
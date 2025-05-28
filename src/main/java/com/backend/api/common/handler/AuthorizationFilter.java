package com.backend.api.common.handler;

import com.backend.api.common.utils.Utils;
import com.backend.api.service.user.IUserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@CrossOrigin(origins = "*")
public class AuthorizationFilter extends BasicAuthenticationFilter {

    final IUserService userService;
    final Environment env;

    public AuthorizationFilter(AuthenticationManager authenticationManager, Environment env, IUserService userService) {
        super(authenticationManager);
        this.env = env;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException, SignatureException, ExpiredJwtException, MalformedJwtException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request, response);

        Utils commonUtils = new Utils();
        String clientRemoteIp = commonUtils.getClientIpAddress(request);

        if (authentication == null) {
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ExpiredJwtException, MalformedJwtException {
        String authorizationHeader = request.getHeader(env.getProperty("authorization.token.header.access-name"));

        String refreshHeader = request.getHeader(env.getProperty("authorization.token.header.refresh-name"));

        if (authorizationHeader == null && refreshHeader == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Authorization header isn't exists");
            return null;
        }

        if (refreshHeader != null && !refreshHeader.startsWith(Objects.requireNonNull(env.getProperty("authorization.token.header.prefix"))) ) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Authorization header isn't exists");
            return null;
        }

        assert authorizationHeader != null;
        String token = authorizationHeader.replace(Objects.requireNonNull(env.getProperty("authorization.token.header.prefix")), "").replaceAll(" ", "");
        Claims claims;
        int claimsUserType;

        // 토큰 파싱
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Objects.requireNonNull(env.getProperty("token.secret")).getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token.trim())
                    .getBody();
            claimsUserType = (int) claims.get("userType");
            long issueAt = claims.getIssuedAt().toInstant().getEpochSecond();

        } catch (MalformedJwtException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Malformed token");
            return null;
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Expired token");
            return null;
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid token");
            return null;
        }

        return new UsernamePasswordAuthenticationToken(claimsUserType, null, new ArrayList<>());
    }
}

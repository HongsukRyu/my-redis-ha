package com.backend.api.common.handler;

import com.backend.api.common.utils.Utils;
import com.backend.api.model.aclpolicy.dto.AclCheckRequest;
import com.backend.api.service.aclpolicy.IAclPolicyService;
import com.backend.api.service.user.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@CrossOrigin(origins = "*")
public class RequestAuthorizationFilter extends BasicAuthenticationFilter {

    final IUserService userService;
    private final IAclPolicyService aclPolicyService;
    private final Environment env;
    private final Utils util;

    public RequestAuthorizationFilter(AuthenticationManager authenticationManager,
                                      IUserService userService, IAclPolicyService aclPolicyService, Environment env, Utils util) {
        super(authenticationManager);
        this.userService = userService;
        this.aclPolicyService = aclPolicyService;
        this.env = env;
        this.util = util;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorizationHeader = request.getHeader(env.getProperty("authorization.token.header.access-name"));

        String refreshHeader = request.getHeader(env.getProperty("authorization.token.header.refresh-name"));

        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (authorizationHeader == null || !authorizationHeader.startsWith(Objects.requireNonNull(env.getProperty("authorization.token.header.prefix")))) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Authorization header isn't exists");
        } else {
            // 토큰 파싱
            String accessToken = authorizationHeader.replace(Objects.requireNonNull(env.getProperty("authorization.token.header.prefix")), "").replaceAll(" ", "");

            Claims claims = tokenParser(request);

            // 토큰 클레임
            String claimsUserId = (String) claims.get("userId");
            int claimsType = (int) claims.get("userType");

            request.setAttribute("userType", claimsType);
            request.setAttribute("userId", claimsUserId);

            // uri 체크
            //noinspection DataFlowIssue
            if (uri != null || uri.length() > 1) {
                uri = Utils.makeSecureString(uri);

                if (aclPolicyService.isMethodAllowed(new AclCheckRequest(uri, util.getStringRoleType(claimsType), method))) {
                    chain.doFilter(request, response);
                } else {
                    response.sendError(HttpStatus.FORBIDDEN.value(), "userType is Not match");
                    log.error("Forbidden request for this userType {}", claimsType);
                }
            }
        }
    }

    private Claims tokenParser(HttpServletRequest request) {
        // 토큰 파싱
        String authorizationHeader = request.getHeader(env.getProperty("authorization.token.header.access-name"));
        String token = authorizationHeader.replace(Objects.requireNonNull(env.getProperty("authorization.token.header.prefix")), "").replaceAll(" ", "");
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Objects.requireNonNull(env.getProperty("token.secret")).getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token.trim())
                .getBody();
    }
}
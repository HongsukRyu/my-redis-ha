package com.backend.api.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.api.common.utils.AuthenticationUtils;
import com.backend.api.common.utils.Utils;
import com.backend.api.model.history.dto.LoginHistoryInfoDto;
import com.backend.api.model.history.dto.LoginTryHistoryInfoDto;
import com.backend.api.model.user.dto.UsersDto;
import com.backend.api.model.user.model.LoginRequestModel;
import com.backend.api.common.object.Const;
import com.backend.api.service.history.ILoginHistoryService;
import com.backend.api.service.history.ILoginTryHistoryService;
import com.backend.api.service.user.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Slf4j
@CrossOrigin(origins = "*")
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final IUserService userService;
    private final Environment env;
    private final AuthenticationUtils utils;

    private final ILoginTryHistoryService loginTryHistoryService;
    private final ILoginHistoryService loginHistoryService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, Environment env,
                                IUserService userService, AuthenticationUtils utils, ILoginHistoryService loginHistoryService, ILoginTryHistoryService loginTryHistoryService) {
        this.userService = userService;
        this.env = env;
        this.utils = utils;
        this.loginHistoryService = loginHistoryService;
        this.loginTryHistoryService = loginTryHistoryService;
        super.setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/api/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            LoginRequestModel creds = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);

            // Log Login Try History
            Utils commonUtils = new Utils();

            LoginTryHistoryInfoDto tryHistInfo = new LoginTryHistoryInfoDto();
            tryHistInfo.setUserId(creds.getUserId());
            tryHistInfo.setUserEmail(creds.getEmail());
            tryHistInfo.setClientIp(commonUtils.getClientIpAddress(request));
            loginTryHistoryService.saveLoginTryHistoryInfo(tryHistInfo);

            // 	인증정보 조회
            UsersDto userDto;
            if (creds.getUserId() != null && !creds.getUserId().isEmpty()) {
                userDto = userService.confirmUser(creds.getUserId(), creds.getPassword());
            } else {
                userDto = userService.confirmUser(creds.getEmail(), creds.getPassword());
            }

            // Account Info Error
            if (userDto == null || !StringUtils.hasText(userDto.getUserId())) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("ERROR :: LOGIN FAILED");
                return null;
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDto.getEmail(),
                    creds.getPassword()
            );
            return getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime now = localDateTime.atZone(ZoneId.of("Asia/Seoul"));
        long issueTime = now.toInstant().toEpochMilli();

        long accessExpiration = issueTime + Long.parseLong(Objects.requireNonNull(env.getProperty("token.access_expiration_time")));
        long refreshExpiration = issueTime + Long.parseLong(Objects.requireNonNull(env.getProperty("token.refresh_expiration_time")));

        String email = ((User) authentication.getPrincipal()).getUsername();
        UsersDto usersDto = userService.getUserDetailsByEmail(email);

        String accessToken = utils.makeAccessToken(usersDto, issueTime, accessExpiration);
        String refreshToken = utils.makeRefreshToken(usersDto.getUserId(), issueTime, refreshExpiration);

        // Log :: Login History Info
        Utils commonUtils = new Utils();
        userService.saveAccountSessionInfo(usersDto.getUserId(), commonUtils.getClientIpAddress(request), issueTime, accessToken, refreshToken);

        String clientRemoteIp = commonUtils.getClientIpAddress(request);

        LoginHistoryInfoDto histInfoDto = new LoginHistoryInfoDto();
        histInfoDto.setUserId(usersDto.getUserId());
        histInfoDto.setClientIp(clientRemoteIp);
        histInfoDto.setUserType(usersDto.getType());
        loginHistoryService.saveLoginHistoryInfo(histInfoDto);

        response.addHeader("message", String.valueOf(Const.LOGIN_SUCCESS));
        response.setContentType("plain/text; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("SUCCESS");

        response.setHeader("AccessToken", accessToken);
        response.setHeader("RefreshToken", refreshToken);
        response.setHeader("exp", (env.getProperty("token.access_expiration_time")));
        response.setStatus(HttpStatus.OK.value());
    }
}
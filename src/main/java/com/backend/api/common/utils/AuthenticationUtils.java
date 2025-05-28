package com.backend.api.common.utils;

import com.backend.api.model.user.dto.UsersDto;
import com.backend.api.service.user.IUserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Data
@NoArgsConstructor
@Component
public class AuthenticationUtils {

    private IUserService userService;
    private Environment env;

    @Autowired
    public AuthenticationUtils(IUserService userService, Environment env) {
        this.userService = userService;
        this.env = env;
    }

    public String makeAccessToken(UsersDto userDto, long issueTime, long access_expiration) {

        return Jwts.builder()
                .setSubject("accessToken")
                .claim("userId", userDto.getUserId())
                .claim("userName", userDto.getName())
                .claim("userType", userDto.getType())
                .claim("userGroupId", userDto.getUserGroupId())
                .setIssuedAt(Date.from(Instant.ofEpochMilli(issueTime)))
                .setExpiration(Date.from(Instant.ofEpochMilli(access_expiration)))
                .signWith(Keys.hmacShaKeyFor(Objects.requireNonNull(env.getProperty("token.secret")).getBytes(StandardCharsets.UTF_8))).compact();
    }

    public String makeRefreshToken(String userId, long issueTime, long refresh_expiration) {

        return Jwts.builder()
                .setSubject("refreshToken")
                .claim("userId", userId)
                .setIssuedAt(Date.from(Instant.ofEpochMilli(issueTime)))
                .setExpiration(Date.from(Instant.ofEpochMilli(refresh_expiration)))
                .signWith(Keys.hmacShaKeyFor(Objects.requireNonNull(env.getProperty("token.secret")).getBytes(StandardCharsets.UTF_8))).compact();
    }
}

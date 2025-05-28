package com.backend.api.controller.account;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.backend.api.common.object.RequestModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.api.common.object.GlobalConst;
import com.backend.api.common.object.Success;
import com.backend.api.common.utils.Utils;
import com.backend.api.model.user.dto.UsersDto;
import com.backend.api.model.user.entity.UserInfo;
import com.backend.api.model.user.entity.Users;
import com.backend.api.model.user.model.CreateUserResponseModel;
import com.backend.api.service.user.IUserInfoService;
import com.backend.api.service.user.IUserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/account")
public class AccountController {
    final IUserService userService;
    final IUserInfoService userInfoService;
    final Environment env;
    final Utils utils;
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public AccountController(IUserService userService, IUserInfoService userInfoService, Environment env, Utils utils) {
        this.userService = userService;
        this.userInfoService = userInfoService;
        this.env = env;
        this.utils = utils;
    }

    @PostMapping("/refreshAccessToken")
    private ResponseEntity<?> getRefreshAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String authorizationHeader = request.getHeader("RefreshToken");
        if (authorizationHeader == null || !authorizationHeader.startsWith(Objects.requireNonNull(env.getProperty("authorization.token.header.prefix")))) {
            log.warn("RefreshToken validation error #1 : refreshToken header isn't exists");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String refreshToken = authorizationHeader.replace(Objects.requireNonNull(env.getProperty("authorization.token.header.prefix")), "").replaceAll(" ", "");

        Claims claims;
        String claimsUserId;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Objects.requireNonNull(env.getProperty("token.secret")).getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(refreshToken.trim())
                    .getBody();
            claimsUserId = (String) claims.get("userId");
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

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        UsersDto userDto = userService.getUserById(claimsUserId);

        if (userService.checkValidRefreshToken(claimsUserId, refreshToken)) {
            Map<String, Object> map = new HashMap<>();
            LocalDateTime ldt = LocalDateTime.now();
            ZonedDateTime now = ldt.atZone(ZoneId.of("Asia/Seoul"));
            long issueTime = now.toInstant().toEpochMilli();
            long access_expiration = issueTime + Long.parseLong(Objects.requireNonNull(env.getProperty("token.access_expiration_time")));
            String accessToken = Jwts.builder()
                    .setSubject("accessToken")
                    .claim("userId", userDto.getUserId())
                    .claim("userName", userDto.getName())
                    .claim("userType", userDto.getType())
                    .setExpiration(Date.from(Instant.ofEpochMilli(access_expiration)))
                    .setHeader(map)
                    .setIssuedAt(Date.from(Instant.ofEpochMilli(issueTime)))
                    .signWith(Keys.hmacShaKeyFor(Objects.requireNonNull(env.getProperty("token.secret")).getBytes(StandardCharsets.UTF_8))).compact();

            headers.add("AccessToken", accessToken);
            headers.add("exp", (env.getProperty("token.access_expiration_time")));
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

            userService.updateLastRefreshTime(userDto.getUserId(), accessToken);
            return new ResponseEntity<>(headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * 계정 생성
     *
     * @param user
     * @return success
     */
    @PostMapping("/createAccount")
    @Operation(summary = "createAccount", description = "계정 생성")
    public ResponseEntity<Success> createAccount(HttpServletRequest request, @RequestBody Users user) {
        Success success = new Success(true);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        try {

            UsersDto usersDto = modelMapper.map(user, UsersDto.class);
            UsersDto createDto = userService.signUpUser(usersDto);
            success.setResult(modelMapper.map(createDto, CreateUserResponseModel.class));

            return ResponseEntity.status(HttpStatus.CREATED).body(success);
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorMsg(e.getMessage());
            success.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(success);
        }
    }

    @PostMapping("/bulk-create")
    public ResponseEntity<Map<String, Object>> bulkCreateAccounts(
            HttpServletRequest request, @RequestBody List<UsersDto> usersDtoList) {

        RequestModel reqModel = utils.getAttributeRequestModel(request);

        if (usersDtoList.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Empty request body"));
        }
        
        List<UserInfo> createdUsers = userInfoService.createUsersInBulk(usersDtoList);

        Map<String, Object> response = new HashMap<>();
        response.put("requestedCount", usersDtoList.size());
        response.put("createdCount", createdUsers.size());
        response.put("createdUsers", createdUsers.stream().map(UserInfo::getName).toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 아이디 중복 체크
     *
     * @return success
     */
    @GetMapping("/checkUserId")
    @Operation(summary = "checkUserId", description = "아이디 중복 체크")
    public ResponseEntity<Success> checkUserIdDuplication(HttpServletRequest request, @RequestParam String userId) {
        Success success = new Success(true);

        UsersDto usersDto = userService.getUserById(userId);
        if (usersDto != null) {
            success.setSuccess(false);
            success.setResult("duplicated");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(success);
        }

        success.setResult("success");
        return ResponseEntity.ok(success);
    }

    /**
     * 아이디 중복 체크
     *
     * @return success
     */
    @GetMapping("/checkEmail")
    @Operation(summary = "checkEmail", description = "이메일 중복 체크")
    public ResponseEntity<Success> checkEmailDuplication(HttpServletRequest request, @RequestParam String email) {
        Success success = new Success(false);

        try {
            UsersDto usersDto = userService.getUserDetailsByEmail(email);
            if (usersDto != null) {
                success.setResult("duplicated");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(success);
            }
        } catch (Exception e) {
            success.setSuccess(true);
            success.setResult("success");
            return ResponseEntity.ok(success);
        }

        success.setResult("success");
        return ResponseEntity.ok(success);
    }

    /**
     * 비밀번호 찾기 API
     *
     * @return ResponseEntity<String>
     */
    @PostMapping("/getAccountPassword")
    @Operation(summary = "getAccountPassword", description = "비밀번호 찾기(계정 확인)")
    public ResponseEntity<String> getAccountPassword(@RequestBody UsersDto usersDto) {
        UsersDto resultUsersDto = userService.getAccountPassword(usersDto);
        String returnValue = "0";
        if (resultUsersDto != null) {
            returnValue = resultUsersDto.getUserId();
        }
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }

    /**
     * 계정 삭제
     *
     * @return success
     */
    @DeleteMapping("/removeAccount/{userId}")
    @Operation(summary = "removeAccount", description = "계정 삭제 API")
    public ResponseEntity<Success> removeAccountInfo(HttpServletRequest request, @PathVariable String userId) {
        userService.removeForceAccount(userId);
        return ResponseEntity.ok(new Success(true));
    }

}
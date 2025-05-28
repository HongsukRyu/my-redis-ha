package com.backend.api.controller.account;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import com.backend.api.common.object.RequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.api.common.utils.Utils;
import com.backend.api.model.user.dto.UsersDto;
import com.backend.api.model.user.entity.Users;
import com.backend.api.service.user.IUserInfoService;
import com.backend.api.service.user.IUserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@WebMvcTest(AccountController.class) // AccountController를 대상으로 웹 MVC 테스트 진행
@TestPropertySource(properties = { // 테스트에 필요한 환경 변수 지정
    "authorization.token.header.prefix=Bearer ",
    "token.secret=testSecretKeyMustBeLongEnoughForHS256Algorithm",
    "token.access_expiration_time=3600000",
    "token.refresh_expiration_time=86400000"
})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTP 요청을 Mock으로 수행

    @Autowired
    private ObjectMapper objectMapper; // JSON 직렬화/역직렬화

    @MockBean // Controller가 의존하는 Bean들을 Mock으로 대체
    private IUserService userService;

    @MockBean
    private IUserInfoService userInfoService;

    @MockBean
    private Utils utils; // 유틸리티 클래스들도 Mock 처리

    // ModelMapper는 컨트롤러 내에서 직접 생성되므로 @MockBean 대상이 아님
    // 필요하다면 AccountController 생성자 주입 방식을 변경하거나 다른 방법 고려

    private Users testUser;
    private UsersDto testUserDto;
    private String testUserId;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID().toString();
        testEmail = "test@example.com";

        testUser = new Users();
        testUser.setUserId(testUserId);
        testUser.setName("Test User");
        testUser.setEmail(testEmail);
        testUser.setEncPassword("encodedPassword");
        testUser.setUserGroupId(1); // 사용자 그룹 ID 예시

        testUserDto = new UsersDto();
        testUserDto.setUserId(testUserId);
        testUserDto.setName("Test User");
        testUserDto.setEmail(testEmail);
        testUserDto.setEncPassword("encodedPassword");
        testUserDto.setUserGroupId(1);
        testUserDto.setType(1L); // Long 타입으로 수정
        testUserDto.setStatus(1); // int 타입으로 수정

        // Mock Environment 설정 제거
    }

    @Test
    @DisplayName("계정 생성 성공 테스트")
    @WithMockUser
    void createAccount_Success() throws Exception {
        // Given
        given(userService.signUpUser(any(UsersDto.class))).willReturn(testUserDto);

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/account/createAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser))
                .with(csrf()));

        // Then
        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.name").value(testUserDto.getName()))
                .andExpect(jsonPath("$.result.email").value(testUserDto.getEmail()));

        verify(userService, times(1)).signUpUser(any(UsersDto.class));
    }

    @Test
    @DisplayName("아이디 중복 확인 테스트 - 중복 없음")
    @WithMockUser
    void checkUserIdDuplication_NotDuplicated() throws Exception {
        // Given
        // userService.getUserById가 호출되면 null (사용자 없음) 반환
        given(userService.getUserById(testUserId)).willReturn(null);

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/account/checkUserId")
                .param("userId", testUserId)); // 쿼리 파라미터로 userId 전달

        // Then
        resultActions.andExpect(status().isOk()) // HTTP 상태 코드 200 OK 확인
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").value("success"));

        verify(userService, times(1)).getUserById(testUserId);
    }

    @Test
    @DisplayName("아이디 중복 확인 테스트 - 중복 있음")
    @WithMockUser
    void checkUserIdDuplication_Duplicated() throws Exception {
        // Given
        // userService.getUserById가 호출되면 testUserDto (사용자 있음) 반환
        given(userService.getUserById(testUserId)).willReturn(testUserDto);

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/account/checkUserId")
                .param("userId", testUserId));

        // Then
        resultActions.andExpect(status().isBadRequest()) // HTTP 상태 코드 400 Bad Request 확인
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.result").value("duplicated"));

        verify(userService, times(1)).getUserById(testUserId);
    }

    @Test
    @DisplayName("이메일 중복 확인 테스트 - 중복 없음")
    @WithMockUser
    void checkEmailDuplication_NotDuplicated() throws Exception {
        // Given
        // userService.getUserDetailsByEmail 호출 시 예외 발생 (사용자 없음)
        given(userService.getUserDetailsByEmail(testEmail)).willThrow(new RuntimeException("User not found")); // 예시 예외

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/account/checkEmail")
                .param("email", testEmail));

        // Then
        // 컨트롤러에서 예외를 catch하고 success: true, result: "success" 반환
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").value("success"));

        verify(userService, times(1)).getUserDetailsByEmail(testEmail);
    }


    @Test
    @DisplayName("이메일 중복 확인 테스트 - 중복 있음")
    @WithMockUser
    void checkEmailDuplication_Duplicated() throws Exception {
        // Given
        // userService.getUserDetailsByEmail 호출 시 testUserDto (사용자 있음) 반환
        given(userService.getUserDetailsByEmail(testEmail)).willReturn(testUserDto);

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/account/checkEmail")
                .param("email", testEmail));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.result").value("duplicated"));

        verify(userService, times(1)).getUserDetailsByEmail(testEmail);
    }

    @Test
    @DisplayName("계정 삭제 테스트")
    @WithMockUser
    void removeAccountInfo() throws Exception {
        // Given
        // userService.removeForceAccount는 void 메서드
        doNothing().when(userService).removeForceAccount(testUserId);

        // utils.getAttributeRequestModel이 null을 반환하지 않도록 Mock 설정
        RequestModel dummyRequestModel = new RequestModel(); // 더미 객체 생성
        dummyRequestModel.setUserId("requestingUserId"); // NPE 방지를 위해 임의의 사용자 ID 설정
        given(utils.getAttributeRequestModel(any())).willReturn(dummyRequestModel);

        // When
        ResultActions resultActions = mockMvc.perform(delete("/api/account/removeAccount/{userId}", testUserId)
                .with(csrf()));

        // Then
        resultActions.andExpect(status().isOk()) // HTTP 상태 코드 200 OK 확인
                .andExpect(jsonPath("$.success").value(true)); // 응답 본문 확인

        verify(userService, times(1)).removeForceAccount(testUserId); // removeForceAccount 호출 검증
    }

    @Test
    @DisplayName("토큰 갱신 성공 테스트")
    @WithMockUser
    void refreshAccessToken_Success() throws Exception {
        // Given: 유효한 Refresh Token 생성
        String tokenSecret = "testSecretKeyMustBeLongEnoughForHS256Algorithm"; // @TestPropertySource 값 사용
        String tokenPrefix = "Bearer ";
        String accessExpirationTime = "3600000";
        long refreshExpiration = System.currentTimeMillis() + Long.parseLong("86400000");
        String validRefreshToken = Jwts.builder()
                .claim("userId", testUserId)
                .setExpiration(Date.from(Instant.ofEpochMilli(refreshExpiration)))
                .signWith(Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        given(userService.getUserById(testUserId)).willReturn(testUserDto);
        given(userService.checkValidRefreshToken(testUserId, validRefreshToken)).willReturn(true);
        doNothing().when(userService).updateLastRefreshTime(eq(testUserId), anyString());

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/account/refreshAccessToken")
                .header("RefreshToken", tokenPrefix + validRefreshToken)
                .with(csrf()));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(header().exists("AccessToken"))
                .andExpect(header().string("exp", accessExpirationTime))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8"));

        verify(userService, times(1)).getUserById(testUserId);
        verify(userService, times(1)).checkValidRefreshToken(testUserId, validRefreshToken);
        verify(userService, times(1)).updateLastRefreshTime(eq(testUserId), anyString());
    }

    @Test
    @DisplayName("토큰 갱신 실패 테스트 - RefreshToken 헤더 없음")
    @WithMockUser
    void refreshAccessToken_Fail_NoHeader() throws Exception {
        // When
        ResultActions resultActions = mockMvc.perform(post("/api/account/refreshAccessToken")
                .with(csrf()));

        // Then
        resultActions.andExpect(status().isBadRequest()); // 400 Bad Request 확인
        verify(userService, never()).getUserById(anyString());
        verify(userService, never()).checkValidRefreshToken(anyString(), anyString());
        verify(userService, never()).updateLastRefreshTime(anyString(), anyString());
    }

     @Test
    @DisplayName("토큰 갱신 실패 테스트 - RefreshToken 유효성 검사 실패")
    @WithMockUser
    void refreshAccessToken_Fail_InvalidTokenCheck() throws Exception {
        // Given: 유효하지 않은 Refresh Token (checkValidRefreshToken가 false 반환하도록 설정)
        String tokenSecret = "testSecretKeyMustBeLongEnoughForHS256Algorithm";
        String tokenPrefix = "Bearer ";
        long refreshExpiration = System.currentTimeMillis() + Long.parseLong("86400000");
         String invalidRefreshToken = Jwts.builder()
                .claim("userId", testUserId)
                .setExpiration(Date.from(Instant.ofEpochMilli(refreshExpiration)))
                .signWith(Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        given(userService.getUserById(testUserId)).willReturn(testUserDto);
        // checkValidRefreshToken에서 false 반환
        given(userService.checkValidRefreshToken(testUserId, invalidRefreshToken)).willReturn(false);

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/account/refreshAccessToken")
                .header("RefreshToken", tokenPrefix + invalidRefreshToken)
                .with(csrf()));

        // Then
        resultActions.andExpect(status().isBadRequest());
        verify(userService, times(1)).getUserById(testUserId);
        verify(userService, times(1)).checkValidRefreshToken(testUserId, invalidRefreshToken);
        verify(userService, never()).updateLastRefreshTime(anyString(), anyString());
    }

    @Test
    @DisplayName("아이디 찾기 성공 테스트")
    @WithMockUser
    void getAccountId_Success() throws Exception {
        // Given
        UsersDto requestDto = new UsersDto();
        requestDto.setName("Test User");
        requestDto.setEmail(testEmail);
        // getAccountId가 호출되면 testUserDto 반환
        given(userService.getAccountId(any(UsersDto.class))).willReturn(testUserDto);

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/account/getAccountId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf()));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(testUserId)); // 응답 본문이 userId 문자열인지 확인

        verify(userService, times(1)).getAccountId(any(UsersDto.class));
    }

    @Test
    @DisplayName("아이디 찾기 실패 테스트 - 사용자 없음")
    @WithMockUser
    void getAccountId_NotFound() throws Exception {
        // Given
        UsersDto requestDto = new UsersDto();
        requestDto.setName("Non Existent User");
        requestDto.setEmail("nonexistent@example.com");
        // getAccountId가 호출되면 null 반환
        given(userService.getAccountId(any(UsersDto.class))).willReturn(null);

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/account/getAccountId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf()));

        // Then
        resultActions.andExpect(status().isOk()) // HTTP 상태 코드 200 OK 확인
                     .andExpect(content().string("0")); // 응답 본문이 "0"인지 확인

        verify(userService, times(1)).getAccountId(any(UsersDto.class));
    }

    @Test
    @DisplayName("비밀번호 찾기(계정 확인) 성공 테스트")
    @WithMockUser
    void getAccountPassword_Success() throws Exception {
        // Given
        UsersDto requestDto = new UsersDto();
        requestDto.setUserId(testUserId);
        requestDto.setName("Test User");
        requestDto.setEmail(testEmail);
        // getAccountPassword가 호출되면 testUserDto 반환
        given(userService.getAccountPassword(any(UsersDto.class))).willReturn(testUserDto);

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/account/getAccountPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf()));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(testUserId)); // 응답 본문이 userId 문자열인지 확인

        verify(userService, times(1)).getAccountPassword(any(UsersDto.class));
    }

    @Test
    @DisplayName("비밀번호 찾기(계정 확인) 실패 테스트 - 사용자 없음")
    @WithMockUser
    void getAccountPassword_NotFound() throws Exception {
        // Given
        UsersDto requestDto = new UsersDto();
        requestDto.setUserId("nonexistent");
        requestDto.setName("Non Existent User");
        requestDto.setEmail("nonexistent@example.com");
        // getAccountPassword가 호출되면 null 반환
        given(userService.getAccountPassword(any(UsersDto.class))).willReturn(null);

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/account/getAccountPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf()));

        // Then
        // 이전에 잘못된 동작(에러 JSON) 기반으로 수정했던 기대값 대신,
        // 정상 동작(200 OK, text/plain, body "0")을 기대하도록 수정합니다.
        resultActions.andExpect(status().isOk()) // 200 OK 확인
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)) // Content-Type 확인
                .andExpect(content().string("0")); // 본문 내용 확인

        // 서비스 메서드 호출 여부 검증은 유지
        verify(userService, times(1)).getAccountPassword(any(UsersDto.class));
    }
} 
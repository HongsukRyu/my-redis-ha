package com.backend.api.service.user;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import com.backend.api.model.account.entity.AccountSessionInfo;
import com.backend.api.model.user.dto.UsersDto;
import com.backend.api.model.user.entity.Users;
import com.backend.api.repository.account.IAccountSessionInfoRepo;
import com.backend.api.repository.user.IUserInfoRepo;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private Environment env;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private IAccountSessionInfoRepo accountSessionInfoRepo;

    @Mock
    private IUserInfoRepo userInfoRepo;

    // ModelMapper는 실제 객체를 사용하되, 특정 메서드 호출을 감시하기 위해 @Spy 사용 가능
    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private UserServiceImpl userService;

    private String testUserId;
    private String testPassword;
    private String testEmail;
    private Users testUserEntity;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID().toString();
        testPassword = "password123";
        testEmail = "test@example.com";

        testUserEntity = new Users();
        testUserEntity.setUserId(testUserId);
        testUserEntity.setName("Test User");
        testUserEntity.setEmail(testEmail);
        testUserEntity.setEncPassword(bCryptPasswordEncoder.encode(testPassword)); // 실제 암호화된 값 설정 (테스트용)
    }

    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    @WithMockUser
    void removeAccountInfo_Success() {
        // Given
        when(bCryptPasswordEncoder.matches(testPassword, testUserEntity.getEncPassword())).thenReturn(true);

        // When
        UsersDto result = userService.removeAccountInfo(testUserId, testPassword);

        // Then
        verify(bCryptPasswordEncoder, times(1)).matches(testPassword, testUserEntity.getEncPassword());
        verify(accountSessionInfoRepo, times(1)).deleteByUserId(testUserId); // 세션 정보 삭제 확인
        verify(modelMapper, times(1)).map(testUserEntity, UsersDto.class); // 결과 매핑 확인
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(testUserId);
        verifyNoInteractions(userInfoRepo); // userInfoRepo 상호작용 없는지 확인
    }

    @Test
    @DisplayName("회원 탈퇴 실패 테스트 - 비밀번호 불일치")
    @WithMockUser
    void removeAccountInfo_PasswordMismatch() {
        // Given
        when(bCryptPasswordEncoder.matches(testPassword, testUserEntity.getEncPassword())).thenReturn(false);

        // When
        UsersDto result = userService.removeAccountInfo(testUserId, testPassword);

        // Then
        verify(bCryptPasswordEncoder, times(1)).matches(testPassword, testUserEntity.getEncPassword());
        verifyNoInteractions(accountSessionInfoRepo); // 세션 정보 삭제 안 함
        verify(modelMapper, never()).map(any(), eq(UsersDto.class)); // 결과 매핑 안 함
        assertThat(result).isNull();
        verifyNoInteractions(userInfoRepo);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 테스트 - 사용자 없음")
    @WithMockUser
    void removeAccountInfo_UserNotFound() {

        // When
        UsersDto result = userService.removeAccountInfo(testUserId, testPassword);

        // Then
        verifyNoInteractions(bCryptPasswordEncoder);
        verifyNoInteractions(accountSessionInfoRepo);
        verify(modelMapper, never()).map(any(), eq(UsersDto.class));
        assertThat(result).isNull();
        verifyNoInteractions(userInfoRepo);
    }

    @Test
    @DisplayName("강제 회원 탈퇴 테스트")
    @WithMockUser
    void removeForceAccount() {
        // When
        userService.removeForceAccount(testUserId);

        // Then
        verify(accountSessionInfoRepo, times(1)).deleteByUserId(testUserId); // 세션 정보 삭제 확인
        verify(userInfoRepo, times(1)).deleteByUserId(testUserId); // 사용자 정보 삭제 확인
        verifyNoInteractions(bCryptPasswordEncoder);
    }

    @Test
    @DisplayName("토큰 삭제 테스트")
    @WithMockUser
    void deleteToken() {
        // When
        userService.deleteToken(testUserId);

        // Then
        verify(accountSessionInfoRepo, times(1)).deleteByUserId(testUserId); // 세션 정보 삭제 확인
        verifyNoInteractions(userInfoRepo);
        // verifyNoInteractions(bCryptPasswordEncoder); // setUp에서 이미 사용되었으므로 이 검증 제거
    }

    @Test
    @DisplayName("계정 세션 정보 저장 테스트 (UserServiceImpl)")
    @WithMockUser
    void saveAccountSessionInfo_shouldSaveSessionInfo() {
        // Given
        String testClientIp = "127.0.0.1";
        long testIssueTime = System.currentTimeMillis();
        String testAccessToken = "access-token";
        String testRefreshToken = "refresh-token";
        String expirationTimeStr = "7200";
        int expectedExpirationTime = 7200;

        when(env.getProperty("token.access_expiration_time")).thenReturn(expirationTimeStr);
        ArgumentCaptor<AccountSessionInfo> sessionInfoCaptor = ArgumentCaptor.forClass(AccountSessionInfo.class);

        // When
        userService.saveAccountSessionInfo(testUserId, testClientIp, testIssueTime, testAccessToken, testRefreshToken);

        // Then
        verify(env, times(1)).getProperty("token.access_expiration_time");
        verify(accountSessionInfoRepo, times(1)).save(sessionInfoCaptor.capture()); // 세션 정보 저장 확인
        verifyNoInteractions(userInfoRepo);
        verifyNoInteractions(bCryptPasswordEncoder);

        AccountSessionInfo savedSessionInfo = sessionInfoCaptor.getValue();
        assertThat(savedSessionInfo).isNotNull();
        assertThat(savedSessionInfo.getUserId()).isEqualTo(testUserId);
        assertThat(savedSessionInfo.getClientIp()).isEqualTo(testClientIp);
        assertThat(savedSessionInfo.getToken()).isEqualTo(testAccessToken);
        assertThat(savedSessionInfo.getRefreshToken()).isEqualTo(testRefreshToken);
        assertThat(savedSessionInfo.getExpirationTime()).isEqualTo(expectedExpirationTime);
        assertThat(savedSessionInfo.getLastLoginDate()).isEqualTo(new Date(testIssueTime));
        assertThat(savedSessionInfo.getLastRefreshTime()).isEqualTo(new Date(testIssueTime));
    }
} 
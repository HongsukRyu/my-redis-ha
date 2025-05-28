package com.backend.api.service.account;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.backend.api.model.account.entity.AccountSessionInfo;
import com.backend.api.repository.account.IAccountSessionInfoRepo;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class) // Mockito 확장 사용
class AccountServiceImplTest {

    @Mock // Mock 객체 생성
    private IAccountSessionInfoRepo accountSessionInfoRepo;

    @Mock // Mock 객체 생성
    private Environment environment;

    @InjectMocks // Mock 객체들을 주입받는 대상
    private AccountServiceImpl accountService;

    private String testUserId;
    private String testAccessToken;
    private String testRefreshToken;
    private String testClientIp;
    private long testIssueTime;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID().toString();
        testAccessToken = "test-access-token";
        testRefreshToken = "test-refresh-token";
        testClientIp = "192.168.0.1";
        testIssueTime = System.currentTimeMillis();
    }

    @Test
    @DisplayName("계정 세션 정보 저장 테스트")
    @WithMockUser
    void saveAccountSessionInfo_shouldSaveSessionInfo() {
        // Given
        String expirationTimeStr = "3600"; // 테스트용 만료 시간 (초 단위)
        int expectedExpirationTime = 3600;

        // Mock 설정: environment.getProperty 호출 시 테스트 값 반환하도록 설정
        when(environment.getProperty("token.access_expiration_time")).thenReturn(expirationTimeStr);
        // Mock 설정: accountSessionInfoRepo.save 호출 시 저장된 엔티티 반환하도록 설정 (void 메서드지만 예시로 추가, 필요 없을 수 있음)
        // when(accountSessionInfoRepo.save(any(AccountSessionInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ArgumentCaptor 생성: save 메서드에 전달된 AccountSessionInfo 객체를 캡처하기 위함
        ArgumentCaptor<AccountSessionInfo> sessionInfoCaptor = ArgumentCaptor.forClass(AccountSessionInfo.class);

        // When
        accountService.saveAccountSessionInfo(testUserId, testClientIp, testIssueTime, testAccessToken, testRefreshToken);

        // Then
        // accountSessionInfoRepo.save 메서드가 1번 호출되었는지 검증하고, 전달된 인자를 캡처
        verify(accountSessionInfoRepo, times(1)).save(sessionInfoCaptor.capture());
        verify(environment, times(1)).getProperty("token.access_expiration_time"); // environment.getProperty가 1번 호출되었는지 검증

        // 캡처된 AccountSessionInfo 객체의 필드 값들이 예상대로 설정되었는지 검증
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

    @Test
    @DisplayName("토큰 제거 테스트")
    @WithMockUser
    void removeToken_shouldCallRepositoryDelete() {
        // When
        accountService.removeToken(testUserId);

        // Then
        // accountSessionInfoRepo.deleteByUserId 메서드가 정확한 userId와 함께 1번 호출되었는지 검증
        verify(accountSessionInfoRepo, times(1)).deleteByUserId(testUserId);
        verifyNoInteractions(environment);
    }
} 
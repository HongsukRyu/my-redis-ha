package com.backend.api.service.account;

import com.backend.api.model.account.entity.AccountSessionInfo;
import com.backend.api.repository.account.IAccountSessionInfoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountServiceImpl implements IAccountService {

    private final IAccountSessionInfoRepo accountSessionInfoRepo;

    final Environment env;

    @Override
    public void saveAccountSessionInfo(String userId, String clientIp, long issueTime, String accessToken, String refreshToken) {

        int expirationTime = Integer.parseInt(Objects.requireNonNull(env.getProperty("token.access_expiration_time")));

        AccountSessionInfo updateSessionInfo = new AccountSessionInfo();
        updateSessionInfo.setUserId(userId);
        updateSessionInfo.setClientIp(clientIp);
        updateSessionInfo.setToken(accessToken);
        updateSessionInfo.setRefreshToken(refreshToken);
        updateSessionInfo.setExpirationTime(expirationTime);
        updateSessionInfo.setLastLoginDate(new Date(issueTime));
        updateSessionInfo.setLastRefreshTime(new Date(issueTime));

        accountSessionInfoRepo.save(updateSessionInfo);

        log.info(userId + " :: 로그인 = " + new Date(issueTime));
    }

    @Override
    public void removeToken(String userId) {

        accountSessionInfoRepo.deleteByUserId(userId);

    }
}
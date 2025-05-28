package com.backend.api.service.account;

public interface IAccountService {
    void saveAccountSessionInfo(String userId, String clientIp, long issueTime, String accessToken, String refreshToken);

    void removeToken(String userId);
}
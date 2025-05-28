package com.backend.api.service.user;

import com.backend.api.model.user.dto.UsersDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {

    UsersDto signUpUser(UsersDto usersDto);

    UsersDto getUserDetailsByEmail(String email);

    UsersDto confirmUser(String id, String password);

    UsersDto getUserById(String userId);

    UsersDto removeAccountInfo(String userId, String Password);

    void removeForceAccount(String userId);

    UsersDto getAccountId(UsersDto usersDto);

    UsersDto getAccountPassword(UsersDto usersDto);

    void deleteToken(String userId);

    boolean checkValidRefreshToken(String claimsUserId, String refreshToken);

    void updateLastRefreshTime(String userId, String accessToken);

    void saveAccountSessionInfo(String userId, String clientIp, long issueTime, String accessToken, String refreshToken);
}
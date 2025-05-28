package com.backend.api.service.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import com.backend.api.model.user.entity.UserInfo;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.api.model.account.entity.AccountSessionInfo;
import com.backend.api.model.user.dto.UsersDto;
import com.backend.api.model.user.entity.Users;
import com.backend.api.repository.account.IAccountSessionInfoRepo;
import com.backend.api.repository.user.IUserInfoRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    final Environment env;
    final BCryptPasswordEncoder bCryptPasswordEncoder;
    final ModelMapper modelMapper = new ModelMapper();

    private final IAccountSessionInfoRepo accountSessionInfoRepo;

    private final IUserInfoRepo userInfoRepo;

    /**
     * 회원 등록
     *
     * @param usersDto
     * @return UsersDto
     */
    @Override
    public UsersDto signUpUser(UsersDto usersDto) {

        usersDto = UsersDto.builder()
                .userId(usersDto.getUserId())
                .name(usersDto.getName())
                .email(usersDto.getEmail())
                .encPassword(bCryptPasswordEncoder.encode(usersDto.getEncPassword()))
                .phone(usersDto.getPhone())
                .type(usersDto.getType())
                .status(usersDto.getStatus())
                .userGroupId(usersDto.getUserGroupId())
                .build();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserInfo userEntity = modelMapper.map(usersDto, UserInfo.class);

        userInfoRepo.save(userEntity);

        return modelMapper.map(userEntity, UsersDto.class);
    }

    @Override
    public UsersDto getUserDetailsByEmail(String email) {

        UserInfo userInfo = userInfoRepo.findByEmail(email);

        if (userInfo == null) {
            throw new UsernameNotFoundException(email);
        }

        return new ModelMapper().map(userInfo, UsersDto.class);
    }

    @Override
    public UsersDto confirmUser(String id, String password) throws UsernameNotFoundException {

        boolean existUser = id.contains("@") ? userInfoRepo.existsByEmail(id) : userInfoRepo.existsByUserId(id);

        if (!existUser) {
            return null;
        }

        UserInfo userEntity = userInfoRepo.findByUserId(id);

        if (bCryptPasswordEncoder.matches(password, userEntity.getEncodedPassword())) {
            return modelMapper.map(userEntity, UsersDto.class);
        } else {
            return null;
        }
    }

    /**
     * 회원 정보 조회
     *
     * @param userId
     * @return UsersDto
     */
    @Override
    public UsersDto getUserById(String userId) {
        UserInfo userEntity = userInfoRepo.findByUserId(userId);
        if (userEntity == null) {
            log.info(String.format("not exists user : %s", userId));
            return null;
        }

        return new ModelMapper().map(userEntity, UsersDto.class);
    }

    @Override
    public UsersDto removeAccountInfo(String userId, String Password) {
        return null;
    }

    @Override
    public void removeForceAccount(String userId) {
        accountSessionInfoRepo.deleteByUserId(userId);
        userInfoRepo.deleteByUserId(userId);
    }

    @Override
    public UsersDto getAccountId(UsersDto usersDto) {
        return null;
    }

    @Override
    public UsersDto getAccountPassword(UsersDto usersDto) {
        return null;
    }

    @Override
    public void deleteToken(String userId) {
        accountSessionInfoRepo.deleteByUserId(userId);
    }

    @Override
    public boolean checkValidRefreshToken(String claimsUserId, String refreshToken) {
        return false;
    }

    @Override
    public void updateLastRefreshTime(String userId, String accessToken) {

    }

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

    /**
     * Spring Security User Detail - Important
     *
     * @param email
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo userEntity = userInfoRepo.findUserDetailsByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException("정보를 찾을 수 없습니다.");
        }

        return new User(userEntity.getEmail() /*DB에서 검색되어진 데이터*/, userEntity.getEncodedPassword(), true, true, true, true, new ArrayList<>()/*role값*/);
    }
}
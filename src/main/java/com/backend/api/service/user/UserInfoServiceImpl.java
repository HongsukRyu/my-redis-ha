package com.backend.api.service.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.api.common.exception.NoDataFoundException;
import com.backend.api.common.object.Const;
import com.backend.api.common.object.Success;
import com.backend.api.common.object.SuccessResult;
import com.backend.api.model.user.dto.UserPointDto;
import com.backend.api.model.user.dto.UsersDto;
import com.backend.api.model.user.entity.UserInfo;
import com.backend.api.model.user.entity.UserPointInfo;
import com.backend.api.repository.role.IRoleInfoRepo;
import com.backend.api.repository.user.IUserGroupInfoRepo;
import com.backend.api.repository.user.IUserInfoRepo;
import com.backend.api.repository.user.IUserPointRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserInfoServiceImpl implements IUserInfoService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper = new ModelMapper();

    private final IUserPointService userPointService;

    private final IUserInfoRepo userInfoRepo;
    private final IUserPointRepo userPointRepo;
    private final IUserGroupInfoRepo userGroupInfoRepo;
    private final IRoleInfoRepo roleInfoRepo;

    /**
     * 회원 정보 조회 - JPA
     *
     * @param userId
     * @return Success
     * @author hs.ryu
     * @since 2023.06.16
     */
    @Override
    public Success getUserInfoById(String userId) {
        Success success = new Success(true);

        try {
            if (!userId.equalsIgnoreCase("")) {
                UserInfo userInfo = userInfoRepo.findByUserId(userId);
                Optional.ofNullable(userInfo).orElseThrow(() -> new NoDataFoundException("User Info not found!"));
                UsersDto userDto = modelMapper.map(userInfo, UsersDto.class);

                UserPointDto userPointDto = userPointService.getUserPoint(null, userId).getResult();

                userDto.setEncPassword(null);
                userDto.setTotalPoint(userPointDto.getTotalPoint());

                success.setResult(userDto);
            } else {
                success.setResult(null);
            }
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorMsg(e.getMessage());
        }

        return success;
    }

    /**
     * 사용자 계정 인증 확인
     *
     * @param userId
     * @param password
     * @return
     * @throws UsernameNotFoundException
     */

    public boolean confirmPwd(String userId, String password) throws UsernameNotFoundException {
        UserInfo userInfo = userInfoRepo.findByUserId(userId);

        return bCryptPasswordEncoder.matches(password, userInfo.getEncodedPassword());
    }

    @Override
    public SuccessResult<UserPointDto> getUserPoint(HttpServletRequest request, String userId) {

        // TODO: Implement the method

        try {
            // Validation userId
            UserInfo userInfo = userInfoRepo.findByUserId(userId);
            if (userInfo == null) {
                throw new UsernameNotFoundException("User not found!");
            }

            // Get UserPointDto from userPointRepo
            UserPointInfo pointInfo = userPointRepo.findByUserId(userId);
            UserPointDto userPointDto = modelMapper.map(pointInfo, UserPointDto.class);

            // Return SuccessResult<UserPointDto>
            return SuccessResult.ok(userPointDto);

        } catch (Exception e) {
            // Return FailResult
            return SuccessResult.error(Const.FAIL, e.getMessage());
        }
    }

    @Transactional
    @Override
    public List<UserInfo> createUsersInBulk(List<UsersDto> usersDtoList) {
        List<UserInfo> userEntities = new ArrayList<>();

        for (UsersDto dto : usersDtoList) {

            if (userInfoRepo.existsByUserId(dto.getUserId())) {
                continue; // 중복 → 스킵
            }

            if (userInfoRepo.existsByEmail(dto.getEmail())) {
                continue; // 중복 → 스킵
            }

            UserInfo entity = new UserInfo();
            entity.setUserId(dto.getUserId());
            entity.setName(dto.getName());
            entity.setEmail(dto.getEmail());
            entity.setEncodedPassword(bCryptPasswordEncoder.encode(dto.getEncPassword()));
            entity.setPhone(dto.getPhone());
//            entity.setType(dto.getType());
            entity.setStatus(dto.getStatus());
            entity.setExpireYn("N");

            userEntities.add(entity);
        }

        // 한 번에 저장

        return userInfoRepo.saveAll(userEntities);
    }
}
package com.backend.api.controller.user;

import com.backend.api.common.object.*;
import com.backend.api.common.object.*;
import com.backend.api.common.utils.Utils;
import com.backend.api.model.user.dto.UserPointDto;
import com.backend.api.model.user.entity.UserInfo;
import com.backend.api.service.user.IUserInfoService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/userInfo")
public class UserInfoController {

    final Environment env;
    final IUserInfoService userInfoService;
    final ModelMapper modelMapper = new ModelMapper();

    private final Utils utils;

    @Autowired
    public UserInfoController(IUserInfoService userInfoService, Environment env, Utils utils) {
        this.userInfoService = userInfoService;
        this.env = env;
        this.utils = utils;
    }

    /**
     * 사용자 계정 정보 조회 API
     *
     * @param request
     * @return success
     */
    @GetMapping("/getAccountInfoByUserId")
    @Operation(summary = "getAccountInfoByUserId", description = "사용자 계정 정보 조회 API")
    public Success getAccountInfoByUserId(HttpServletRequest request, @RequestParam String userId) {

        return userInfoService.getUserInfoById(userId);
    }

    /**
     * 사용자 포인트 정보 조회 API
     *
     * @param request
     * @return SuccessResult
     */
    @GetMapping("/getUserPoint")
    @Operation(summary = "getUserPoint", description = "사용자 포인트 정보 조회 API")
    public SuccessResult<UserPointDto> getUserPoint(HttpServletRequest request) {

        RequestModel reqModel = utils.getAttributeRequestModel(request);
        String userId = reqModel.getUserId();

        return userInfoService.getUserPoint(request, userId);
    }
}
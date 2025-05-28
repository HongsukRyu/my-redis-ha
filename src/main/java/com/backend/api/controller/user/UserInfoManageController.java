package com.backend.api.controller.user;

import com.backend.api.common.object.Success;
import com.backend.api.common.utils.Utils;
import com.backend.api.service.user.IUserInfoManageService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/manage/account")
public class UserInfoManageController {

    final Environment env;
    final Utils utils;
    final IUserInfoManageService userManageService;

    @Autowired
    public UserInfoManageController(IUserInfoManageService userManageService, Environment env, Utils utils) {
        this.userManageService = userManageService;
        this.env = env;
        this.utils = utils;
    }

    /**
     * 사용자 계정 정보 조회 API (관리자 페이지)
     *
     * @param request
     * @return success
     */
    @GetMapping("/")
    @Operation(summary = "getAccountInfoByPage", description = "사용자 계정 조회 API")
    public Success getAccountInfoByPage(HttpServletRequest request,
        @RequestParam (defaultValue = "0") int page,
        @RequestParam (defaultValue = "10") int range,
        @RequestParam (defaultValue = "0", required = false) Long classId,
        @RequestParam (defaultValue = "0", required = false) int roleType) {

        return userManageService.getAccountInfoByPage(range, page, classId, roleType);
    }

}
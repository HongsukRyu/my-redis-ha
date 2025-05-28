package com.backend.api.controller.user;

import com.backend.api.common.object.RequestModel;
import com.backend.api.common.object.Success;
import com.backend.api.common.object.SuccessResult;
import com.backend.api.common.object.*;
import com.backend.api.common.utils.Utils;
import com.backend.api.model.user.dto.UserPointDto;
import com.backend.api.model.user.dto.UserPointTransactionDto;
import com.backend.api.service.user.IUserPointService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/point")
public class UserPointController {

    final Environment env;
    final IUserPointService userPointService;
    final ModelMapper modelMapper = new ModelMapper();

    private final Utils utils;

    @Autowired
    public UserPointController(IUserPointService userPointService, Environment env, Utils utils) {
        this.userPointService = userPointService;
        this.env = env;
        this.utils = utils;
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

        return userPointService.getUserPoint(request, userId);
    }

    /**
     * 사용자 포인트 정보 조회 API
     *
     * @param request
     * @return SuccessResult
     */
    @GetMapping("/getUserAllPoint")
    @Operation(summary = "getUserAllPoint", description = "사용자 포인트 정보 조회 API")
    public ResponseEntity<Success> getUserAllPoint(HttpServletRequest request) {

        RequestModel reqModel = utils.getAttributeRequestModel(request);
        String userId = reqModel.getUserId();

        Success success = userPointService.getUserAllPoint(request, userId);
        if (success.isSuccess()) {
            return ResponseEntity.ok().body(success);
        } else {
            return ResponseEntity.badRequest().body(success);
        }
    }

    @PostMapping("/earn")
    public SuccessResult<String> earnPoints(HttpServletRequest request,
        @RequestBody UserPointTransactionDto dto) {

        String assignedBy = utils.getAttributeRequestModel(request).getUserId();
        dto.setAssignedBy(assignedBy);

        return userPointService.earnPoints(dto);
    }
}
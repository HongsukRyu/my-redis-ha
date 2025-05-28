package com.backend.api.service.user;

import com.backend.api.common.object.Success;
import com.backend.api.common.object.SuccessResult;
import com.backend.api.model.user.dto.UserPointDto;
import com.backend.api.model.user.dto.UserPointTransactionDto;

import jakarta.servlet.http.HttpServletRequest;


public interface IUserPointService {

    SuccessResult<UserPointDto> getUserPoint(HttpServletRequest request, String userId);

    Success getUserAllPoint(HttpServletRequest request, String userId);

    SuccessResult<String> earnPoints(UserPointTransactionDto dto);
}
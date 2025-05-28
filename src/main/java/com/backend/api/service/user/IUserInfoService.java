package com.backend.api.service.user;

import com.backend.api.common.object.Success;
import com.backend.api.common.object.SuccessResult;
import com.backend.api.model.user.dto.UserPointDto;
import com.backend.api.model.user.dto.UsersDto;
import com.backend.api.model.user.entity.UserInfo;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface IUserInfoService {

    Success getUserInfoById(String userId);

    SuccessResult<UserPointDto> getUserPoint(HttpServletRequest request, String userId);

    List<UserInfo> createUsersInBulk(List<UsersDto> usersDtoList);
}
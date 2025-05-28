package com.backend.api.service.user;

import com.backend.api.common.object.Success;

public interface IUserInfoManageService {

    Success getAccountInfoByPage(int range, int page, Long classId, int roleType);
}
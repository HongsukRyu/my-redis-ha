package com.backend.api.service.role;

import com.backend.api.common.object.Success;

import java.util.Map;

public interface IRoleService {

    Success getRoleInfoByPage(int range, int page, String searchQuery, String sort, String orderBy, Map<String, String> conditionMap);
}
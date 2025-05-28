package com.backend.api.service.tenant;


import com.backend.api.common.object.Success;

public interface ITenantInfoService {

    /**
     * Tenant Info API
     *
     */
//    Success getTenantInfoByPage(int range, int page, String searchQuery, String sort, String orderBy);

    Success getTenantInfoList();

    Success getTenantInfoById(String tenantId);
}

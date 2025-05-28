package com.backend.api.service.tenant;


import com.backend.api.common.object.Success;
import com.backend.api.model.tenant.entity.AllowedTenantInfo;

import java.util.List;

public interface IAllowedTenantInfoService {

    /**
     * Allowed Tenant Info API
     *
     */

    Success getListByUserGroupId(int userGroupId);

    List<AllowedTenantInfo> getAvailableList(int userGroupId);

    Success getDetailInfoById(Long id);

}

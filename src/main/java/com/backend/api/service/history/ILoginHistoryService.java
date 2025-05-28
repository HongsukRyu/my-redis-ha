package com.backend.api.service.history;

import com.backend.api.model.history.dto.LoginHistoryInfoDto;
import com.backend.api.common.object.Success;

public interface ILoginHistoryService {

    /**
     * Login History Info Paging 처리 API
     *
     * @author hs.ryu
     * @since 2023.05.09
     * @param range
     * @param page
     * @param searchQuery
     * @param sort
     * @param orderBy
     * @return
     */
    Success getLoginHistoryInfoByPage(int range, int page, String searchQuery, String sort, String orderBy);

    Success getLoginHistoryInfoById(int id);

    Success setLoginHistoryInfo(LoginHistoryInfoDto loginHistoryInfoDto);

    void saveLoginHistoryInfo(LoginHistoryInfoDto loginHistoryInfoDto);

    Success removeLoginHistoryInfo(Long userId, int id);

}

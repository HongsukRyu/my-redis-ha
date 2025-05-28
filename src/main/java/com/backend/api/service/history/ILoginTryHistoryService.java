package com.backend.api.service.history;

import com.backend.api.model.history.dto.LoginTryHistoryInfoDto;
import com.backend.api.common.object.Success;

public interface ILoginTryHistoryService {

    /**
     * Login Try History Info Paging 처리 API
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
    Success getLoginTryHistoryInfoByPage(int range, int page, String searchQuery, String sort, String orderBy);

    Success getLoginTryHistoryInfoById(int id);

    Success setLoginTryHistoryInfo(LoginTryHistoryInfoDto loginTryHistoryInfoDto);

    void saveLoginTryHistoryInfo(LoginTryHistoryInfoDto loginTryHistoryInfoDto);

    Success removeLoginTryHistoryInfo(Long userId, int id);

}

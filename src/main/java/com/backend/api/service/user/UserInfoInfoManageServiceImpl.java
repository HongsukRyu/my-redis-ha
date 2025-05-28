package com.backend.api.service.user;

import com.backend.api.common.exception.NoDataFoundException;
import com.backend.api.common.object.Success;
import com.backend.api.model.user.entity.UserGroupInfo;
import com.backend.api.repository.user.IUserInfoRepo;
import com.backend.api.repository.user.IUserGroupInfoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserInfoInfoManageServiceImpl implements IUserInfoManageService {

    private final IUserInfoRepo userInfoRepo;

    private final IUserGroupInfoRepo userGroupInfoRepo;

    @Override
    public Success getAccountInfoByPage(int range, int page, Long classId, int roleType) {
        Success success = new Success(true);

        String sort = "userId";
        String orderBy = "asc";
        // 초기 기본 키 설정

        PageRequest pageReq = PageRequest.of(page, range, Sort.by(sort).ascending());
        Page<?> userInfoList = userInfoRepo.findAll(pageReq);
        Optional.ofNullable(userInfoList).orElseThrow( ()->new NoDataFoundException("User Info List not found!"));
        success.setResult(userInfoList);

        return success;
    }
}
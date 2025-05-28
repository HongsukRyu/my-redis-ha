package com.backend.api.service.history;

import com.backend.api.common.object.Const;
import com.backend.api.model.history.dto.LoginHistoryInfoDto;
import com.backend.api.model.history.entity.LoginHistoryInfo;
import com.backend.api.repository.history.ILoginHistoryInfoRepo;
import com.backend.api.common.exception.NoDataFoundException;
import com.backend.api.common.object.Success;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoginHistoryServiceImpl implements ILoginHistoryService {

    private final ILoginHistoryInfoRepo loginHistoryInfoRepo;

    final ModelMapper modelMapper = new ModelMapper();

    @Override
    public Success getLoginHistoryInfoByPage(int range, int page, String searchQuery, String sort, String orderBy) {
        Success success = new Success(true);

        // 초기 기본 키 설정
        if(sort == null || sort.isEmpty()) {
            sort = "createDate";
            orderBy = "desc";
        }

        page = 0;
        PageRequest pageReq = PageRequest.of(page, range, orderBy.endsWith("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending());
        Page<LoginHistoryInfo> loginHistInfo = loginHistoryInfoRepo.findAll(pageReq);

        Optional.ofNullable(loginHistInfo).orElseThrow(()->new NoDataFoundException("Login History Info List not found!"));

        Page<LoginHistoryInfoDto> loginHistIntoList = loginHistInfo.map(LoginHistoryInfo -> modelMapper.map(LoginHistoryInfo, LoginHistoryInfoDto.class));
        success.setResult(loginHistIntoList);

        return success;
    }

    @Override
    public Success getLoginHistoryInfoById(int id) {
        Success success = new Success(false);

        Optional<LoginHistoryInfo> loginHistInfoOptional = loginHistoryInfoRepo.findById(id);
        LoginHistoryInfo loginHistoryInfo = loginHistInfoOptional.orElseThrow(() -> new NoDataFoundException("noticeInfo is not found!"));

        Optional<LoginHistoryInfoDto> loginHistoryInfoDto = loginHistInfoOptional.map(LoginHistoryInfo -> modelMapper.map(LoginHistoryInfo, LoginHistoryInfoDto.class));

        success.setResult(loginHistoryInfoDto);
        success.setSuccess(true);

        return success;
    }

    @Override
    public Success setLoginHistoryInfo(LoginHistoryInfoDto loginHistoryInfoDto) {
        Success success = new Success(false);

        try {
            Optional<LoginHistoryInfo> loginHistOptional = loginHistoryInfoRepo.findById(loginHistoryInfoDto.getId());
            LoginHistoryInfo loginHistoryInfo = loginHistOptional.orElseThrow(() -> new NoDataFoundException("The data is not found!"));

            loginHistoryInfoRepo.save(loginHistoryInfo);
            success.setSuccess(true);
        } catch (NoDataFoundException e) {
            success.setErrorCode(String.valueOf(e.hashCode()));
            success.setErrorMsg(e.getMessages());
        } catch (Exception e) {
            success.setErrorCode(String.valueOf(e.hashCode()));
            success.setErrorMsg(e.getMessage());
        }

        return success;
    }

    @Override
    public void saveLoginHistoryInfo(LoginHistoryInfoDto loginHistoryInfoDto) {
        Success success = new Success(false);

        try {
            LoginHistoryInfo loginHistoryInfo = modelMapper.map(loginHistoryInfoDto, LoginHistoryInfo.class);
            loginHistoryInfoRepo.save(loginHistoryInfo);
            success.setSuccess(true);
        } catch (Exception e) {
            success.setErrorMsg(e.getMessage());
            success.setErrorCode(Const.FAIL);
        }

    }

    @Override
    public Success removeLoginHistoryInfo(Long userId, int id) {
        Success success = new Success(true);
        Optional<LoginHistoryInfo> loginHistInfoOptional = loginHistoryInfoRepo.findById(id);
        LoginHistoryInfo loginHistoryInfo = loginHistInfoOptional.orElseThrow(() -> new NoDataFoundException("LoginHistoryInfo is not found!"));

        if(loginHistoryInfo.getId() < 1) {
            success.setSuccess(false);
        }
        // Manager 권한 체크~!

        loginHistoryInfoRepo.deleteById(id);
        return success;
    }
}

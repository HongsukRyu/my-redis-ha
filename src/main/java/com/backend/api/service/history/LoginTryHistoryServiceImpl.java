package com.backend.api.service.history;

import com.backend.api.common.object.Const;
import com.backend.api.model.history.dto.LoginTryHistoryInfoDto;
import com.backend.api.model.history.entity.LoginTryHistoryInfo;
import com.backend.api.repository.history.ILoginTryHistoryInfoRepo;
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
public class LoginTryHistoryServiceImpl implements ILoginTryHistoryService {

    private final ILoginTryHistoryInfoRepo loginTryHistoryInfoRepo;

    final ModelMapper modelMapper = new ModelMapper();

    @Override
    public Success getLoginTryHistoryInfoByPage(int range, int page, String searchQuery, String sort, String orderBy) {
        Success success = new Success(true);

        // 초기 기본 키 설정
        if(sort == null || sort.isEmpty()) {
            sort = "createDate";
            orderBy = "desc";
        }

        page = 0;
        PageRequest pageReq = PageRequest.of(page, range, orderBy.endsWith("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending());
        Page<LoginTryHistoryInfo> loginTryHistInfo = loginTryHistoryInfoRepo.findAll(pageReq);

        Optional.ofNullable(loginTryHistInfo).orElseThrow(()->new NoDataFoundException("Login Try History Info List not found!"));

        Page<LoginTryHistoryInfoDto> loginTryHistInfoList = loginTryHistInfo.map(LoginTryHistoryInfo -> modelMapper.map(LoginTryHistoryInfo, LoginTryHistoryInfoDto.class));
        success.setResult(loginTryHistInfoList);

        return success;
    }

    @Override
    public Success getLoginTryHistoryInfoById(int id) {
        Success success = new Success(false);

        Optional<LoginTryHistoryInfo> loginTryHistInfoOptional = loginTryHistoryInfoRepo.findById(id);
        LoginTryHistoryInfo loginTryHistoryInfo = loginTryHistInfoOptional.orElseThrow(() -> new NoDataFoundException("Login Try History Info is not found!"));

        Optional<LoginTryHistoryInfoDto> loginTryHistoryInfoDto = loginTryHistInfoOptional.map(LoginTryHistoryInfo -> modelMapper.map(LoginTryHistoryInfo, LoginTryHistoryInfoDto.class));

        success.setResult(loginTryHistoryInfoDto);
        success.setSuccess(true);

        return success;
    }

    @Override
    public Success setLoginTryHistoryInfo(LoginTryHistoryInfoDto loginTryHistoryInfoDto) {
        Success success = new Success(false);

        try {
            Optional<LoginTryHistoryInfo> loginTryHistOptional = loginTryHistoryInfoRepo.findById(loginTryHistoryInfoDto.getId());
            LoginTryHistoryInfo loginTryHistoryInfo = loginTryHistOptional.orElseThrow(() -> new NoDataFoundException("The data is not found!"));

            loginTryHistoryInfoRepo.save(loginTryHistoryInfo);
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
    public void saveLoginTryHistoryInfo(LoginTryHistoryInfoDto loginTryHistoryInfoDto) {
        Success success = new Success(false);

        try {
            LoginTryHistoryInfo loginTryHistInfo = modelMapper.map(loginTryHistoryInfoDto, LoginTryHistoryInfo.class);

            loginTryHistoryInfoRepo.save(loginTryHistInfo);
            success.setSuccess(true);
        } catch (Exception e) {
            success.setErrorMsg(e.getMessage());
            success.setErrorCode(Const.FAIL);
        }

    }

    @Override
    public Success removeLoginTryHistoryInfo(Long userId, int id) {
        Success success = new Success(true);
        Optional<LoginTryHistoryInfo> loginTryHistInfoOptional = loginTryHistoryInfoRepo.findById(id);
        LoginTryHistoryInfo loginTryHistoryInfo = loginTryHistInfoOptional.orElseThrow(() -> new NoDataFoundException("LoginHistoryInfo is not found!"));

        if(loginTryHistoryInfo.getId() < 1) {
            success.setSuccess(false);
        }
        // Manager 권한 체크~!

        loginTryHistoryInfoRepo.deleteById(id);
        return success;
    }
}

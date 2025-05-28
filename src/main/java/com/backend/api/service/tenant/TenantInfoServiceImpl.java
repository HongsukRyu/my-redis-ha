package com.backend.api.service.tenant;

import com.backend.api.common.exception.NoDataFoundException;
import com.backend.api.common.object.Success;
import com.backend.api.model.tenant.dto.TenantInfoDto;
import com.backend.api.model.tenant.entity.TenantInfo;
import com.backend.api.repository.tenant.ITenantInfoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class TenantInfoServiceImpl implements ITenantInfoService {

    private final ITenantInfoRepo tenantInfoRepo;

    final ModelMapper modelMapper = new ModelMapper();

//    @Override
//    public Success getTenantInfoByPage(int range, int page, String searchQuery, String sort, String orderBy) {
//        Success success = new Success(false);
//
//        // 초기 기본 키 설정
//        if(sort == null || sort.isEmpty()) {
//            sort = "createDate";
//            orderBy = "desc";
//        }
//
//        page = 0;
//        PageRequest pageReq = PageRequest.of(page, range, orderBy.endsWith("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending());
//        Page<TenantInfo> tenantInfos = tenantInfoRepo.findAll(pageReq);
//
//        Optional.ofNullable(tenantInfos).orElseThrow(()->new NoDataFoundException("tenant config Info List not found!"));
//
//        Page<TenantInfoDto> intoList = tenantInfos.map(TenantInfo -> modelMapper.map(TenantInfo, TenantInfoDto.class));
//        success.setSuccess(true);
//        success.setResult(intoList);
//        return success;
//    }

    @Override
    public Success getTenantInfoList() {

        Success success = new Success(false);

        try {
            List<TenantInfo> tenantInfoList = tenantInfoRepo.findAll();
            Optional.ofNullable(tenantInfoList).orElseThrow(()->new NoDataFoundException("tenant config Info List not found!"));

            List<TenantInfoDto> tenantInfoDtoList = tenantInfoList.stream().map(
                    tenantInfo -> modelMapper.map(tenantInfo, TenantInfoDto.class)).toList();
            success.setSuccess(true);
            success.setResult(tenantInfoDtoList);
        } catch (Exception e) {
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
            return success;
        }

        return success;
    }

    @Override
    public Success getTenantInfoById(String tenantId) {
        Success success = new Success(false);

        try {
            Optional<TenantInfo> infoOptional = tenantInfoRepo.findByTenantId(tenantId);
            TenantInfo tenantInfo = infoOptional.orElseThrow(() -> new NoDataFoundException("tenant config info is not found!"));

            success.setResult(tenantInfo);
            success.setSuccess(true);
        } catch (Exception e) {
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
            return success;
        }

        return success;
    }
}

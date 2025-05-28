package com.backend.api.service.tenant;

import com.backend.api.common.exception.NoDataFoundException;
import com.backend.api.common.object.Success;
import com.backend.api.model.tenant.dto.AllowedTenantInfoDto;
import com.backend.api.model.tenant.entity.AllowedTenantInfo;
import com.backend.api.repository.tenant.IAllowedTenantInfoRepo;
import com.backend.api.repository.tenant.ITenantInfoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class AllowedTenantInfoServiceImpl implements IAllowedTenantInfoService {

    private final IAllowedTenantInfoRepo allowedTenantInfoRepo;
    private final ITenantInfoRepo tenantInfoRepo;

    final ModelMapper modelMapper = new ModelMapper();


    @Override
    public Success getListByUserGroupId(int userGroupId) {
        Success success = new Success(false);

        try {
            List<AllowedTenantInfo> infoList = allowedTenantInfoRepo.findAllByUserGroupId(userGroupId);
            if (infoList.isEmpty()) {
                throw new NoDataFoundException("allowed tenant Info List not found!");
            }

            List<AllowedTenantInfoDto> dtoList = infoList.stream()
                    .map(AllowedTenantInfo -> modelMapper.map(AllowedTenantInfo, AllowedTenantInfoDto.class))
                    .collect(Collectors.toList());

            dtoList.forEach(dto -> {
                tenantInfoRepo.findByTenantId(dto.getTenantId())
                        .ifPresent(dto::setTenantName);
            });
            success.setSuccess(true);
            success.setResult(dtoList);
        } catch (Exception e) {
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
            return success;
        }

        return success;
    }

    public List<AllowedTenantInfo> getAvailableList(int userGroupId) {

        try {
            List<AllowedTenantInfo> infoList = allowedTenantInfoRepo.findAllByUserGroupId(userGroupId);
            if (infoList.isEmpty()) {
                throw new NoDataFoundException("allowed tenant Info List not found!");
            }
            return infoList;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public Success getDetailInfoById(Long id) {

        Success success = new Success(false);

        try {
            Optional<AllowedTenantInfo> info = allowedTenantInfoRepo.findById(id);
            info.orElseThrow(()->new NoDataFoundException("allowed tenant Info not found!"));

            success.setSuccess(true);
            success.setResult(modelMapper.map(info.get(), AllowedTenantInfoDto.class));
        } catch (Exception e) {
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
            return success;
        }

        return success;
    }
}

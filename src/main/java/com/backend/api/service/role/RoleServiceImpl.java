package com.backend.api.service.role;

import com.backend.api.repository.role.IRoleInfoRepo;
import com.backend.api.common.config.BaseSpecification;
import com.backend.api.common.exception.NoDataFoundException;
import com.backend.api.common.object.Success;
import com.backend.api.model.role.dto.RoleInfoDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements IRoleService {

    private final IRoleInfoRepo roleInfoRepo;

    final ModelMapper modelMapper = new ModelMapper();

    @Override
    public Success getRoleInfoByPage(int range, int page, String searchQuery, String sort, String orderBy,
                                     Map<String, String> conditionMap) {
        Success success = new Success(true);

        // 초기 기본 키 설정
        if(sort == null || sort.isEmpty()) {
            sort = "roleId";
            orderBy = "desc";
        }

        PageRequest pageReq = PageRequest.of(page, range, orderBy.equals("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending());
        Page<?> roleList = roleInfoRepo.findAll(BaseSpecification.makeSpec(searchQuery, conditionMap), pageReq);
        Optional.of(roleList).orElseThrow( ()->new NoDataFoundException("Role Info List not found!"));
        Page<RoleInfoDto> roleInfoList = roleList.map(RoleInfo -> modelMapper.map(RoleInfo, RoleInfoDto.class));

        success.setResult(roleInfoList);
        return success;
    }

}
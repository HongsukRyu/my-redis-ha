package com.backend.api.controller.tenant;

import com.backend.api.common.object.Const;
import com.backend.api.common.object.RequestModel;
import com.backend.api.common.object.Success;
import com.backend.api.common.utils.Utils;
import com.backend.api.service.tenant.IAllowedTenantInfoService;
import com.backend.api.service.tenant.ITenantInfoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value ="/api/users/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final ITenantInfoService tenantInfoService;
    private final IAllowedTenantInfoService allowedTenantInfoService;

    private final Utils utils;

    // 선도기업은 해당 테넌트의 정보만 전달
    // 파트너사는 전체 테넌트 정보 조회
    @GetMapping(value ="/getList")
    @Operation(summary = "getList", description = "테넌트 리스트 조회")
    public ResponseEntity<Success> getList(
            HttpServletRequest request) {

        Success success = new Success(true);

        RequestModel reqModel = utils.getAttributeRequestModel(request);

        int userGroupId = reqModel.getUserGroupId();

        try {
            success = allowedTenantInfoService.getListByUserGroupId(userGroupId);
        } catch (Exception e) {
            success.setErrorCode(Const.FAIL);
            success.setErrorMsg(e.getMessage());
            success.setSuccess(false);
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(success);
        }

        return ResponseEntity.ok(success);
    }

}
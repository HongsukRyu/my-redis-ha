package com.backend.api.controller.role;

import com.backend.api.common.utils.MessageLogger;
import com.backend.api.service.role.IRoleService;
import com.backend.api.common.object.Success;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping(value ="/api/role")
public class RoleController {

    private final IRoleService iRoleService;

    private static final MessageLogger logger = new MessageLogger(LoggerFactory.getLogger(RoleController.class));

    @GetMapping(value ="/getRoleInfoList")
    @Operation(summary = "getRoleInfoList", description = "사용자 타입 조회")
    public Success getRoleInfoByPage(
            HttpServletRequest request,
            @RequestParam("range") int range,
            @RequestParam("page") int page,
            @RequestParam(value = "searchQuery", required = false) String searchQuery,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "orderBy", required = false) String orderBy) {

        searchQuery = URLDecoder.decode(searchQuery, StandardCharsets.UTF_8);

        Map<String, String> conditionMap = new HashMap<>();

        return iRoleService.getRoleInfoByPage(range, page, searchQuery, sort, orderBy, conditionMap);
    }
}
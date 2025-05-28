package com.backend.api.controller.history;

import com.backend.api.service.history.ILoginHistoryService;
import com.backend.api.service.history.ILoginTryHistoryService;
import com.backend.api.common.object.Success;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    private final ILoginHistoryService loginHistoryService;

    private final ILoginTryHistoryService loginTryHistoryService;

    /**
     * backend login history 리스트 조회
     */
    @GetMapping("/getLoginHistoryByPage")
    @Operation(summary = "getLoginHistoryByPage", description = "로그인 이력 조회")
    public Success getLoginHistoryByPage(
            @RequestParam(value = "range", defaultValue = "10") int range,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "searchQuery", required = false) String searchQuery,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "orderBy", required = false) String orderBy) {

        return loginHistoryService.getLoginHistoryInfoByPage(range, page, searchQuery, sort, orderBy);
    }

    /**
     * Login Try History 조회
     * @return
     */
    @GetMapping("/getLoginTryHistoryByPage")
    @Operation(summary = "getLoginTryHistoryByPage", description = "로그인 시도 이력 조회")
    public Success getLoginTryHistoryInfoByPage(
            @RequestParam(value = "range", defaultValue = "10") int range,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "searchQuery", required = false) String searchQuery,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "orderBy", required = false) String orderBy) {

        return loginTryHistoryService.getLoginTryHistoryInfoByPage(range, page, searchQuery, sort, orderBy);
    }
}

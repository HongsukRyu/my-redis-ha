package com.backend.api.controller.aclpolicy;

import com.backend.api.model.aclpolicy.dto.AclCheckRequest;
import com.backend.api.service.aclpolicy.IAclPolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/acl/policy")
public class AclPolicyController {

    private final IAclPolicyService aclPolicyService;

    public AclPolicyController(IAclPolicyService aclPolicyService) {
        this.aclPolicyService = aclPolicyService;
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkAccess(@RequestBody AclCheckRequest request) {
        boolean allowed = aclPolicyService.isMethodAllowed(request);
        return ResponseEntity.ok(allowed);
    }


}
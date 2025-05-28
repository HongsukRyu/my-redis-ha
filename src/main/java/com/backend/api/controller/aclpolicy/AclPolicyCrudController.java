package com.backend.api.controller.aclpolicy;

import com.backend.api.model.aclpolicy.dto.AclPolicyRequest;
import com.backend.api.model.aclpolicy.entity.AclPolicy;
import com.backend.api.service.aclpolicy.IAclPolicyCrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/acl/policies")
public class AclPolicyCrudController {

    private final IAclPolicyCrudService aclPolicyCrudService;

    public AclPolicyCrudController(IAclPolicyCrudService aclPolicyCrudService) {
        this.aclPolicyCrudService = aclPolicyCrudService;
    }

    @GetMapping
    public ResponseEntity<List<AclPolicy>> getAllPolicies() {
        return ResponseEntity.ok(aclPolicyCrudService.getAllPolicies());
    }

    @GetMapping("/{uri}")
    public ResponseEntity<AclPolicy> getPolicy(@PathVariable String uri) {
        return aclPolicyCrudService.getPolicyByUri(uri)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AclPolicy> create(@RequestBody AclPolicyRequest request) {
        return ResponseEntity.ok(aclPolicyCrudService.createOrUpdate(request));
    }

    @PutMapping("/{uri}")
    public ResponseEntity<AclPolicy> update(
            @PathVariable String uri,
            @RequestBody AclPolicyRequest request) {
        request.setUri(uri);
        return ResponseEntity.ok(aclPolicyCrudService.createOrUpdate(request));
    }

    @DeleteMapping("/{uri}")
    public ResponseEntity<Void> delete(@PathVariable String uri) {
        aclPolicyCrudService.deleteByUri(uri);
        return ResponseEntity.noContent().build();
    }


}
package com.ndash.idsphere.integrations.controller;

import com.ndash.idsphere.integrations.dto.IntegrationRoleResponse;
import com.ndash.idsphere.integrations.dto.IntegrationUserRequest;
import com.ndash.idsphere.integrations.dto.IntegrationUserResponse;
import com.ndash.idsphere.integrations.registry.IntegrationServiceRegistry;
import com.ndash.idsphere.integrations.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/integrations")
@RestController
@CrossOrigin("*")
public class IntegrationController {

    private final IntegrationServiceRegistry registry;
    private final CheckoutService checkoutService;

    public IntegrationController(IntegrationServiceRegistry registry, CheckoutService checkoutService) {
        this.registry = registry;
        this.checkoutService = checkoutService;
    }

    @PostMapping("/{service}/create-user")
    public ResponseEntity<IntegrationUserResponse> createUser(
            @PathVariable String service,
            @RequestBody IntegrationUserRequest request) {

        var integrationService = registry.getService(service)
                .orElseThrow(() -> new IllegalArgumentException("Unknown integration service: " + service));

        var response = integrationService.createUser(request);
        checkoutService.markProcessedTrue(request.checkoutId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{service}/roles")
    public ResponseEntity<?> getRoles(@PathVariable String service) {
        var integrationService = registry.getService(service)
                .orElseThrow(() -> new IllegalArgumentException("Unknown integration service: " + service));
        var response = integrationService.getRoles();
        return ResponseEntity.ok(response);
    }
}

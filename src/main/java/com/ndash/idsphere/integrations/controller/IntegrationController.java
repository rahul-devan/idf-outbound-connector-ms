package com.ndash.idsphere.integrations.controller;

import com.ndash.idsphere.integrations.domain.Application;
import com.ndash.idsphere.integrations.dto.IntegrationRoleResponse;
import com.ndash.idsphere.integrations.dto.IntegrationUserRequest;
import com.ndash.idsphere.integrations.dto.IntegrationUserResponse;
import com.ndash.idsphere.integrations.registry.IntegrationServiceRegistry;
import com.ndash.idsphere.integrations.service.ApplicationService;
import com.ndash.idsphere.integrations.service.CheckoutService;
import com.ndash.idsphere.integrations.service.IntegrationMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/integrations")
@RestController
@CrossOrigin("*")
public class IntegrationController {

    private final IntegrationServiceRegistry registry;
    private final CheckoutService checkoutService;
    private final IntegrationMetadataService integrationMetadataService;
    private final ApplicationService applicationService;

    public IntegrationController(
            IntegrationServiceRegistry registry,
            CheckoutService checkoutService,
            IntegrationMetadataService integrationMetadataService,
            ApplicationService applicationService
    ) {
        this.registry = registry;
        this.checkoutService = checkoutService;
        this.integrationMetadataService = integrationMetadataService;
        this.applicationService = applicationService;
    }

    @PostMapping("/{service}/create-user")
    public ResponseEntity<IntegrationUserResponse> createUser(
            @PathVariable String service,
            @RequestBody IntegrationUserRequest request) {

        applicationService.requireActive(service);

        var integrationService = registry.getService(service)
                .orElseThrow(() -> new IllegalArgumentException("Unknown integration service: " + service));

        var response = integrationService.createUser(request);
        checkoutService.markProcessedTrue(request.checkoutId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{service}/projects")
    public ResponseEntity<?> getProjects(@PathVariable String service) {
        return ResponseEntity.ok(getProjectsForService(requireActiveApplication(service)));
    }

    @PostMapping("/{service}/projects/sync")
    public ResponseEntity<?> syncProjects(@PathVariable String service) {
        return ResponseEntity.ok(syncProjectsForService(requireActiveApplication(service)));
    }

    @GetMapping("/{service}/roles")
    public ResponseEntity<List<IntegrationRoleResponse>> getRoles(@PathVariable String service) {
        return ResponseEntity.ok(getRolesForService(requireActiveApplication(service)));
    }

    @PostMapping("/{service}/roles/sync")
    public ResponseEntity<List<IntegrationRoleResponse>> syncRoles(@PathVariable String service) {
        return ResponseEntity.ok(syncRolesForService(requireActiveApplication(service)));
    }

    private Application requireActiveApplication(String service) {
        return applicationService.requireActive(service);
    }

    private Object getProjectsForService(Application application) {
        return switch (applicationService.resolveIntegrationCode(application)) {
            case "github" -> integrationMetadataService.getGitHubProjects();
            case "jira" -> integrationMetadataService.getJiraProjects();
            default -> integrationMetadataService.getProjects(
                    applicationService.resolveIntegrationLookupCode(application),
                    Object.class
            );
        };
    }

    private Object syncProjectsForService(Application application) {
        return switch (applicationService.resolveIntegrationCode(application)) {
            case "github" -> integrationMetadataService.syncGitHubProjects();
            case "jira" -> integrationMetadataService.syncJiraProjects();
            default -> integrationMetadataService.syncProjects(
                    applicationService.resolveIntegrationLookupCode(application),
                    Object.class
            );
        };
    }

    private List<IntegrationRoleResponse> getRolesForService(Application application) {
        return switch (applicationService.resolveIntegrationCode(application)) {
            case "github" -> integrationMetadataService.getGitHubRoles(null);
            case "jira" -> integrationMetadataService.getJiraRoles();
            default -> integrationMetadataService.getRoles(
                    applicationService.resolveIntegrationLookupCode(application),
                    null
            );
        };
    }

    private List<IntegrationRoleResponse> syncRolesForService(Application application) {
        return switch (applicationService.resolveIntegrationCode(application)) {
            case "github" -> integrationMetadataService.syncGitHubRoles(null);
            case "jira" -> integrationMetadataService.syncJiraRoles();
            default -> integrationMetadataService.syncRoles(
                    applicationService.resolveIntegrationLookupCode(application),
                    null
            );
        };
    }
}

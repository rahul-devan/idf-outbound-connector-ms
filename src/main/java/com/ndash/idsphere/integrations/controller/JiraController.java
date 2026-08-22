package com.ndash.idsphere.integrations.controller;

import com.ndash.idsphere.integrations.dto.jira.JiraProjectResponse;
import com.ndash.idsphere.integrations.dto.IntegrationRoleResponse;
import com.ndash.idsphere.integrations.service.IntegrationMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integrations/jira")
@CrossOrigin("*")
public class JiraController {

    private final IntegrationMetadataService integrationMetadataService;

    public JiraController(IntegrationMetadataService integrationMetadataService) {
        this.integrationMetadataService = integrationMetadataService;
    }

    @GetMapping("/projects")
    public ResponseEntity<List<JiraProjectResponse>> getProjects() {
        return ResponseEntity.ok(integrationMetadataService.getJiraProjects());
    }

    @PostMapping("/projects/sync")
    public ResponseEntity<List<JiraProjectResponse>> syncProjects() {
        return ResponseEntity.ok(integrationMetadataService.syncJiraProjects());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<IntegrationRoleResponse>> getRoles() {
        return ResponseEntity.ok(integrationMetadataService.getJiraRoles());
    }

    @PostMapping("/roles/sync")
    public ResponseEntity<List<IntegrationRoleResponse>> syncRoles() {
        return ResponseEntity.ok(integrationMetadataService.syncJiraRoles());
    }
}

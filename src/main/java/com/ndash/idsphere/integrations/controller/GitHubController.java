package com.ndash.idsphere.integrations.controller;

import com.ndash.idsphere.integrations.dto.github.GitHubProjectResponse;
import com.ndash.idsphere.integrations.dto.IntegrationRoleResponse;
import com.ndash.idsphere.integrations.service.IntegrationMetadataService;
import com.ndash.idsphere.integrations.service.impl.GitHubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integrations")
@CrossOrigin("*")
public class GitHubController {

    private final GitHubService gitHubService;
    private final IntegrationMetadataService integrationMetadataService;

    public GitHubController(
            GitHubService gitHubService,
            IntegrationMetadataService integrationMetadataService
    ) {
        this.gitHubService = gitHubService;
        this.integrationMetadataService = integrationMetadataService;
    }

    @GetMapping("/github/orgs")
    public ResponseEntity<List<String>> getGitHubOrgs() {
        return ResponseEntity.ok(gitHubService.getOrganizations());
    }

    @GetMapping("/github/projects")
    public ResponseEntity<List<GitHubProjectResponse>> getGitHubProjects() {
        return ResponseEntity.ok(integrationMetadataService.getGitHubProjects());
    }

    @PostMapping("/github/projects/sync")
    public ResponseEntity<List<GitHubProjectResponse>> syncGitHubProjects() {
        return ResponseEntity.ok(integrationMetadataService.syncGitHubProjects());
    }

    @GetMapping("/github/{org}/roles")
    public ResponseEntity<List<IntegrationRoleResponse>> getGitHubRoles(@PathVariable String org) {
        return ResponseEntity.ok(integrationMetadataService.getGitHubRoles(org));
    }

    @PostMapping("/github/{org}/roles/sync")
    public ResponseEntity<List<IntegrationRoleResponse>> syncGitHubRoles(@PathVariable String org) {
        return ResponseEntity.ok(integrationMetadataService.syncGitHubRoles(org));
    }

}

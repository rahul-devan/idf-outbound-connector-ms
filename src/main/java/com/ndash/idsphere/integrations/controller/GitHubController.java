package com.ndash.idsphere.integrations.controller;

import com.ndash.idsphere.integrations.service.impl.GitHubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/integrations")
@CrossOrigin("*")
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/github/orgs")
    public ResponseEntity<?> getGitHubOrgs() {
        return ResponseEntity.ok(gitHubService.getOrganizations());
    }

    @GetMapping("/github/{org}/roles")
    public ResponseEntity<?> getGitHubRoles(@PathVariable String org) {
        if(org.isEmpty()) {
            return ResponseEntity.ok(gitHubService.getRoles());
        } else {
            return ResponseEntity.ok(gitHubService.getRoles(org));
        }
    }

}

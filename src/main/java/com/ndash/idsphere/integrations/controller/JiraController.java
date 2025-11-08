package com.ndash.idsphere.integrations.controller;

import com.ndash.idsphere.integrations.dto.jira.JiraProjectResponse;
import com.ndash.idsphere.integrations.service.impl.JiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/integrations/jira")
@CrossOrigin("*")
public class JiraController {
    private final JiraService jiraService;

    public JiraController(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    @GetMapping("/projects")
    public ResponseEntity<List<JiraProjectResponse>> getProjects() {
        return ResponseEntity.ok(jiraService.getProjects());
    }
}

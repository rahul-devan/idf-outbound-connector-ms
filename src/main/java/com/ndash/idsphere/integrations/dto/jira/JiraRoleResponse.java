package com.ndash.idsphere.integrations.dto.jira;

import java.util.List;
import java.util.Map;

public record JiraRoleResponse(
        String id,
        String name,
        String description,
        String self,
        Map<String, Object> scope, // contains "type" and "project"
        List<Map<String, Object>> actors // optional
) {}

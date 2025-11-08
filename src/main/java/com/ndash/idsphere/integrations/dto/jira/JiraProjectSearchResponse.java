package com.ndash.idsphere.integrations.dto.jira;

import java.util.List;

public record JiraProjectSearchResponse(
        int maxResults,
        int startAt,
        int total,
        boolean isLast,
        List<JiraProjectResponse> values
) {}
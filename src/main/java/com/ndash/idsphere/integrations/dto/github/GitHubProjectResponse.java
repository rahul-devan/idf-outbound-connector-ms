package com.ndash.idsphere.integrations.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubProjectResponse(
        String id,
        Integer number,
        String title,
        String url,
        String shortDescription,
        boolean closed
) {}

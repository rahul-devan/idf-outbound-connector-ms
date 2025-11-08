package com.ndash.idsphere.integrations.dto.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JiraProjectResponse(
        String id,
        String key,
        String name,
        String projectTypeKey,
        boolean simplified,
        boolean isPrivate,
        Map<String, String> avatarUrls
) {}

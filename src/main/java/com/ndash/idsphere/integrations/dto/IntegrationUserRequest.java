package com.ndash.idsphere.integrations.dto;

public record IntegrationUserRequest(
        String email, String displayName,
        String projectKey,
        String roleId ) {
}

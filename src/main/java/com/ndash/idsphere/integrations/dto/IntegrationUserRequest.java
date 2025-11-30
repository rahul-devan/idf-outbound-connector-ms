package com.ndash.idsphere.integrations.dto;

import java.util.UUID;

public record IntegrationUserRequest(
        String email, String displayName,
        String projectKey,
        String roleId, UUID checkoutId) {
}

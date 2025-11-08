package com.ndash.idsphere.integrations.service;

import com.ndash.idsphere.integrations.dto.IntegrationRoleResponse;
import com.ndash.idsphere.integrations.dto.IntegrationUserRequest;
import com.ndash.idsphere.integrations.dto.IntegrationUserResponse;

import java.util.List;

public interface IntegrationService {
    String getServiceName();
    IntegrationUserResponse createUser(IntegrationUserRequest request);
    List<IntegrationRoleResponse> getRoles();
}

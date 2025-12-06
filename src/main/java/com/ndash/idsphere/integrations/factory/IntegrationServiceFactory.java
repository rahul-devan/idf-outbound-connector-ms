package com.ndash.idsphere.integrations.factory;

import com.ndash.idsphere.integrations.service.IntegrationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IntegrationServiceFactory {

    private final Map<String, IntegrationService> services;

    public IntegrationServiceFactory(List<IntegrationService> list) {
        this.services = list.stream()
                .collect(Collectors.toMap(IntegrationService::getServiceName, s -> s));
    }

    public IntegrationService get(String name) {
        return services.get(name);
    }
}

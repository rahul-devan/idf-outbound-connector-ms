package com.ndash.idsphere.integrations.registry;

import com.ndash.idsphere.integrations.service.IntegrationService;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class IntegrationServiceRegistry {

    private final Map<String, IntegrationService> services = new HashMap<>();

    public IntegrationServiceRegistry(List<IntegrationService> serviceList) {
        serviceList.forEach(s -> services.put(s.getServiceName().toLowerCase(), s));
    }

    public Optional<IntegrationService> getService(String name) {
        return Optional.ofNullable(services.get(name.toLowerCase()));
    }
}

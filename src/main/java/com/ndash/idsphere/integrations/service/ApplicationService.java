package com.ndash.idsphere.integrations.service;

import com.ndash.idsphere.integrations.domain.Application;
import com.ndash.idsphere.integrations.exception.IntegrationException;
import com.ndash.idsphere.integrations.repositories.ApplicationRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public Application requireActive(String integrationCode) {
        return applicationRepository.findByIntegrationNameIgnoreCase(integrationCode)
                .or(() -> applicationRepository.findByNameIgnoreCase(integrationCode))
                .filter(Application::isActive)
                .orElseThrow(() -> new IntegrationException(
                        "UNKNOWN_CONNECTOR",
                        "Unknown or inactive connector: " + integrationCode
                ));
    }

    public String resolveIntegrationCode(Application application) {
        if (application.getIntegrationName() != null && !application.getIntegrationName().isBlank()) {
            return application.getIntegrationName().toLowerCase();
        }
        return application.getName().toLowerCase();
    }

    public String resolveIntegrationLookupCode(Application application) {
        if (application.getIntegrationName() != null && !application.getIntegrationName().isBlank()) {
            return application.getIntegrationName();
        }
        return application.getName();
    }
}

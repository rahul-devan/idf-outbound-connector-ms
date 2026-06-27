package com.ndash.idsphere.integrations.dto.hr;

import com.ndash.idsphere.integrations.domain.Blueprint;
import com.ndash.idsphere.integrations.domain.BlueprintApplicationRole;
import com.ndash.idsphere.integrations.domain.JobTitle;
import com.ndash.idsphere.integrations.repositories.ApplicationRepository;
import com.ndash.idsphere.integrations.repositories.BlueprintRepository;
import org.springframework.stereotype.Service;

@Service
public class BlueprintService {

    private final BlueprintRepository repo;
    private final ApplicationRepository applicationRepository;

    public BlueprintService(BlueprintRepository repo, ApplicationRepository applicationRepository) {
        this.repo = repo;
        this.applicationRepository = applicationRepository;
    }

    public Blueprint createIfNotExists(String name) {

        return repo.findAll().stream()
                .filter(b -> b.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    Blueprint bp = new Blueprint();
                    bp.setName(name);
                    addEssentialApplications(bp);
                    return repo.save(bp);
                });
    }

    public void linkJobTitle(Blueprint blueprint, JobTitle jobTitle) {
        blueprint.getJobTitles().add(jobTitle);
        repo.save(blueprint);
    }

    private void addEssentialApplications(Blueprint blueprint) {

        applicationRepository.findByEssentialTrue()
                .forEach(application -> {

                    BlueprintApplicationRole mapping =
                            new BlueprintApplicationRole();

                    mapping.setBlueprint(blueprint);
                    mapping.setApplication(application);
                    mapping.setApplicationRole(null);
                    mapping.setRoleName("User"); // Default role

                    blueprint.getApplicationRoles().add(mapping);
                });
    }
}

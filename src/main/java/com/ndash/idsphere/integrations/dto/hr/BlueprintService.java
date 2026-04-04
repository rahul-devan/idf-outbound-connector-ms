package com.ndash.idsphere.integrations.dto.hr;

import com.ndash.idsphere.integrations.domain.Blueprint;
import com.ndash.idsphere.integrations.domain.JobTitle;
import com.ndash.idsphere.integrations.repositories.BlueprintRepository;
import org.springframework.stereotype.Service;

@Service
public class BlueprintService {

    private final BlueprintRepository repo;

    public BlueprintService(BlueprintRepository repo) {
        this.repo = repo;
    }

    public Blueprint createIfNotExists(String name) {

        return repo.findAll().stream()
                .filter(b -> b.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    Blueprint bp = new Blueprint();
                    bp.setName(name);
                    return repo.save(bp);
                });
    }

    public void linkJobTitle(Blueprint blueprint, JobTitle jobTitle) {
        blueprint.getJobTitles().add(jobTitle);
        repo.save(blueprint);
    }
}

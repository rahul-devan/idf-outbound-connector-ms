package com.ndash.idsphere.integrations.dto.hr;

import com.ndash.idsphere.integrations.domain.JobTitle;
import com.ndash.idsphere.integrations.repositories.JobTitleRepository;
import org.springframework.stereotype.Service;

@Service
public class JobTitleService {

    private final JobTitleRepository repo;

    public JobTitleService(JobTitleRepository repo) {
        this.repo = repo;
    }

    public JobTitle upsert(String name, String source) {

        if (name == null || name.isBlank()) {
            name = "UNKNOWN";
        }

        String finalName = name;
        return repo.findByNameAndExternalSource(name, source)
                .orElseGet(() -> {
                    JobTitle jt = new JobTitle();
                    jt.setName(finalName);
                    jt.setExternalSource(source);
                    return repo.save(jt);
                });
    }
}
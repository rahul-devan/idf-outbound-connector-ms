package com.ndash.idsphere.integrations.dto.hr;

import com.ndash.idsphere.integrations.domain.Department;
import com.ndash.idsphere.integrations.repositories.DepartmentRepository;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    private final DepartmentRepository repo;

    public DepartmentService(DepartmentRepository repo) {
        this.repo = repo;
    }

    public Department upsert(String extId, String name, String source) {

        return repo.findByExternalIdAndExternalSource(extId, source)
                .map(dept -> {
                    dept.setName(name);
                    return repo.save(dept);
                })
                .orElseGet(() -> {
                    Department dept = new Department();
                    dept.setExternalId(extId);
                    dept.setExternalSource(source);
                    dept.setName(name);
                    return repo.save(dept);
                });
    }

    public Department getOrCreateDefault(String name, String extId, String source) {

        return repo.findByExternalIdAndExternalSource(extId, source)
                .orElseGet(() -> {
                    Department dept = new Department();
                    dept.setName(name);
                    dept.setExternalId(extId);
                    dept.setExternalSource(source);
                    return repo.save(dept);
                });
    }
}

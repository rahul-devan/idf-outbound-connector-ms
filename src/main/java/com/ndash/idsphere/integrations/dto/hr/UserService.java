package com.ndash.idsphere.integrations.dto.hr;

import com.ndash.idsphere.integrations.domain.Blueprint;
import com.ndash.idsphere.integrations.domain.Department;
import com.ndash.idsphere.integrations.domain.JobTitle;
import com.ndash.idsphere.integrations.domain.User;
import com.ndash.idsphere.integrations.domain.enums.ExternalSource;
import com.ndash.idsphere.integrations.domain.enums.UserSource;
import com.ndash.idsphere.integrations.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User upsert(HrEmployee emp, Department dept, ExternalSource src, Blueprint blueprint, JobTitle jobTitle) {

        return repo.findByEmail(emp.getEmail())
                .map(user -> {
                    user.setFirstName(extractFirstName(emp.getName()));
                    user.setLastName(extractLastName(emp.getName()));
                    user.setJobTitleName(emp.getJobTitle());
                    user.setDepartment(dept);
                    user.setExternalId(emp.getId().toString());
                    user.setExternalSource(src.name());
                    user.setSource(UserSource.HR);
                    user.setLastSyncedAt(LocalDateTime.now());
                    user.setJobTitle(jobTitle);
                    return repo.save(user);
                })
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(emp.getEmail());
                    user.setFirstName(extractFirstName(emp.getName()));
                    user.setLastName(extractLastName(emp.getName()));
                    user.setJobTitleName(emp.getJobTitle());
                    user.setDepartment(dept);
                    user.setExternalId(emp.getId().toString());
                    user.setExternalSource(src.name());
                    user.setSource(UserSource.HR);
                    user.setActive(true);
                    user.setJobTitle(jobTitle);
                    user.setLastSyncedAt(LocalDateTime.now());
                    return repo.save(user);
                });
    }

    private String extractFirstName(String name) {
        return name != null ? name.split(" ")[0] : null;
    }

    private String extractLastName(String name) {
        return name != null && name.contains(" ")
                ? name.substring(name.indexOf(" ") + 1)
                : null;
    }
}

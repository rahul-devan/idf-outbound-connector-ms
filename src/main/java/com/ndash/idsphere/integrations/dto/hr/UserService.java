package com.ndash.idsphere.integrations.dto.hr;

import com.ndash.idsphere.integrations.domain.*;
import com.ndash.idsphere.integrations.domain.enums.ExternalSource;
import com.ndash.idsphere.integrations.domain.enums.UserSource;
import com.ndash.idsphere.integrations.repositories.RoleRepository;
import com.ndash.idsphere.integrations.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository repo;
    private final RoleRepository roleRepository;

    public UserService(UserRepository repo, RoleRepository roleRepository) {
        this.repo = repo;
        this.roleRepository = roleRepository;
    }

    public User upsert(HrEmployee emp, Department dept, ExternalSource src, Blueprint blueprint, JobTitle jobTitle) {

        if (emp.getEmail() == null || emp.getEmail().isBlank()) {
            throw new IllegalArgumentException("Employee email is missing");
        }

        Role defaultRole = roleRepository.findByName("user").orElse(null);


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
                    mapManager(emp, user);
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(defaultRole);
                    user.setUserRoles(Set.of(userRole));
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
                    mapManager(emp, user);
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(defaultRole);
                    user.setUserRoles(Set.of(userRole));
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

    public void mapManager(HrEmployee hrEmployee, User user) {

        Integer managerExternalId = hrEmployee.getManagerId();

        if (managerExternalId != null) {
            repo.findByExternalId(String.valueOf(managerExternalId))
                    .ifPresent(user::setManager);
        } else {
            user.setManager(null);
        }
    }
}

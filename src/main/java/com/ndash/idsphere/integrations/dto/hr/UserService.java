package com.ndash.idsphere.integrations.dto.hr;

import com.ndash.idsphere.integrations.domain.*;
import com.ndash.idsphere.integrations.domain.enums.ExternalSource;
import com.ndash.idsphere.integrations.domain.enums.UserSource;
import com.ndash.idsphere.integrations.repositories.RoleRepository;
import com.ndash.idsphere.integrations.repositories.UserRepository;
import com.ndash.idsphere.integrations.service.AzureADService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repo;
    private final RoleRepository roleRepository;
    private final AzureADService azureADService;
    private final PasswordEncoder passwordEncoder;

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
                    assignDefaultRole(user, defaultRole);
                    user.setBlueprint(blueprint);
                    user.setPassword(passwordEncoder.encode("Test@123"));
                    checkInAzureAD(user);
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
                    assignDefaultRole(user, defaultRole);
                    user.setBlueprint(blueprint);
                    user.setPassword(passwordEncoder.encode("Test@123"));
                    checkInAzureAD(user);
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

    private void assignDefaultRole(User user, Role defaultRole) {

        if (defaultRole == null) {
            throw new RuntimeException("Default role 'user' not found");
        }

        boolean exists = user.getUserRoles()
                .stream()
                .anyMatch(ur ->
                        ur.getRole() != null &&
                                ur.getRole().getId().equals(defaultRole.getId())
                );

        if (exists) {
            return;
        }

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(defaultRole);

        user.getUserRoles().add(userRole);
    }


    public void checkInAzureAD(User user) {
        try {
            if (user.getAzureId() == null) {
                log.info("Creating user in Azure AD for email={}", user.getEmail());
                com.microsoft.graph.models.User azureUser = azureADService.createUser(user.getFirstName(), user.getEmail());
                user.setAzureId(azureUser != null ? azureUser.id : null);
                log.info("User created in Azure AD with id={}", user.getAzureId());
            }
        } catch (Exception e) {
            log.error("Failed to create user in Azure AD for email={}", user.getEmail());
        }
    }
}

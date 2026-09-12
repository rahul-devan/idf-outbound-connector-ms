package com.ndash.idsphere.integrations.service;

import com.ndash.idsphere.integrations.domain.User;
import com.ndash.idsphere.integrations.domain.enums.UserSource;
import com.ndash.idsphere.integrations.dto.hr.HrEmployee;
import com.ndash.idsphere.integrations.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrUserSyncService {

    private static final String ODOO = "ODOO";

    private final HrClientService odooEmployeeService;
    private final UserRepository userRepository;

    @Transactional
    public void syncOdooUsers() {

        log.info("Starting Odoo employee synchronization");

        // IMPORTANT:
        // If this throws, transaction stops and no users are deactivated.
        List<HrEmployee> odooEmployees =
                odooEmployeeService.getEmployees();

        Set<String> activeOdooEmployeeIds = odooEmployees.stream()
                .map(HrEmployee::getId)
                .map(String::valueOf)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        log.info("Received {} employees from Odoo", activeOdooEmployeeIds.size());

        List<User> hrUsers =
                userRepository.findBySourceAndExternalSource(
                        UserSource.HR,
                        ODOO
                );

        int deactivatedCount = 0;

        for (User user : hrUsers) {

            String externalId = user.getExternalId();

            if (externalId == null) {
                continue;
            }

            if (!activeOdooEmployeeIds.contains(externalId)
                    && user.isActive()) {

                user.setActive(false);
                deactivatedCount++;

                log.info(
                        "Deactivating Odoo user. userId={}, externalId={}, email={}",
                        user.getId(),
                        externalId,
                        user.getEmail()
                );
            }
        }

        userRepository.saveAll(hrUsers);

        log.info(
                "Odoo employee synchronization completed. " +
                        "Odoo employees={}, DB HR users={}, deactivated={}",
                activeOdooEmployeeIds.size(),
                hrUsers.size(),
                deactivatedCount
        );
    }
}

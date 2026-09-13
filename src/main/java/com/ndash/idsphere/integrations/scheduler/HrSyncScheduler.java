package com.ndash.idsphere.integrations.scheduler;

import com.ndash.idsphere.integrations.service.HrUserSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HrSyncScheduler {

    private final HrUserSyncService hrUserSyncService;

    @Scheduled(cron = "${scheduler.hr-sync.cron: 0 0 * * * *}") // Default: every hour
    public void syncHrUsers() {

        log.info("Starting scheduled HR user sync");

        try {
            hrUserSyncService.syncOdooUsers();
        } catch (Exception e) {
            log.error("HR user synchronization failed", e);
        }
    }
}

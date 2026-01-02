package com.flowbill.project.scheduler;

import com.flowbill.project.entity.Sprint;
import com.flowbill.project.repository.SprintRepository;
import com.flowbill.project.service.BurndownService;
import com.flowbill.project.config.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Scheduled job to create daily snapshots for all active sprints.
 * Runs daily at midnight to capture accurate burndown data.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DailySnapshotScheduler {

    private final SprintRepository sprintRepository;
    private final BurndownService burndownService;

    /**
     * Creates daily snapshots for all ACTIVE sprints across all tenants.
     * Runs every day at midnight (00:00).
     */
    @Scheduled(cron = "0 0 0 * * *") // Midnight every day
    @Transactional
    public void createDailySnapshots() {
        log.info("Starting daily snapshot creation for all active sprints");

        try {
            // Get all active sprints (across all tenants)
            List<Sprint> activeSprints = sprintRepository.findByStatus(Sprint.SprintStatus.ACTIVE);

            log.info("Found {} active sprints for snapshot creation", activeSprints.size());

            for (Sprint sprint : activeSprints) {
                try {
                    // Temporarily set tenant context for this sprint
                    TenantContext.setCurrentTenant(sprint.getTenantId());
                    TenantContext.setCurrentUserId(0L); // System user

                    burndownService.createDailySnapshot(sprint.getId());
                    log.debug("Created snapshot for sprint {} (tenant: {})",
                            sprint.getId(), sprint.getTenantId());

                } catch (Exception e) {
                    log.error("Failed to create snapshot for sprint {} (tenant: {}): {}",
                            sprint.getId(), sprint.getTenantId(), e.getMessage(), e);
                    // Continue with other sprints even if one fails
                } finally {
                    TenantContext.clear();
                }
            }

            log.info("Daily snapshot creation completed. Processed {} sprints", activeSprints.size());

        } catch (Exception e) {
            log.error("Fatal error during daily snapshot creation: {}", e.getMessage(), e);
        }
    }

    /**
     * Manual trigger for testing purposes.
     * Can be called via an admin endpoint if needed.
     */
    public void triggerManualSnapshot() {
        log.info("Manual snapshot trigger initiated");
        createDailySnapshots();
    }
}

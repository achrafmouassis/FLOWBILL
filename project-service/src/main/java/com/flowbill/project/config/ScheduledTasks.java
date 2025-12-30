package com.flowbill.project.config;

import com.flowbill.project.entity.Sprint;
import com.flowbill.project.repository.SprintRepository;
import com.flowbill.project.service.BurndownService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final BurndownService burndownService;
    private final SprintRepository sprintRepository;

    /**
     * Executes daily at 1 AM to create snapshots for all active sprints.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void createDailySnapshots() {
        log.info("Starting daily burndown snapshots...");
        List<Sprint> activeSprints = sprintRepository.findAll().stream()
                .filter(s -> s.getStatus() == Sprint.SprintStatus.ACTIVE)
                .collect(java.util.stream.Collectors.toList());

        for (Sprint sprint : activeSprints) {
            try {
                // We need to set tenant context if multi-tenancy is based on ThreadLocal
                // In a scheduled task, there's no request.
                // However, the snapshot's prePersist uses TenantContext.getCurrentTenant().
                // We might need to iterate tenants or set it from sprint.tenantId.
                com.flowbill.project.config.TenantContext.setCurrentTenant(sprint.getTenantId());
                burndownService.createDailySnapshot(sprint.getId());
            } catch (Exception e) {
                log.error("Failed to create snapshot for sprint {}: {}", sprint.getId(), e.getMessage());
            } finally {
                com.flowbill.project.config.TenantContext.clear();
            }
        }
    }
}

package com.flowbill.project.service;

import com.flowbill.project.entity.ActivityLog;
import com.flowbill.project.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Transactional
    public void logActivity(String eventType, String entityType, Long entityId, String message) {
        try {
            String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
            Long userId = com.flowbill.project.config.TenantContext.getCurrentUserId();

            ActivityLog log = new ActivityLog();
            log.setTenantId(tenantId);
            log.setEventType(eventType);
            log.setActorUserId(userId != null ? userId : 0L);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setMessage(message);

            activityLogRepository.save(log);
        } catch (Exception e) {
            // Logging should not break business flow
            System.err.println("Failed to log activity: " + e.getMessage());
        }
    }
}

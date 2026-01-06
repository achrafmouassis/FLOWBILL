package com.flowbill.project.controller;

import com.flowbill.project.entity.ActivityLog;
import com.flowbill.project.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogRepository activityLogRepository;

    @GetMapping("/timeline")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
    public List<ActivityLog> getTimeline() {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        return activityLogRepository.findTop20ByTenantIdOrderByCreatedAtDesc(tenantId);
    }
}

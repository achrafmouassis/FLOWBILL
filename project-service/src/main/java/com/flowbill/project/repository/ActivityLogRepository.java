package com.flowbill.project.repository;

import com.flowbill.project.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop20ByTenantIdOrderByCreatedAtDesc(String tenantId);
}

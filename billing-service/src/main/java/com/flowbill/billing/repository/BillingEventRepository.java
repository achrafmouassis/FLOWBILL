package com.flowbill.billing.repository;

import com.flowbill.billing.entity.BillingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BillingEventRepository extends JpaRepository<BillingEvent, Long> {
    List<BillingEvent> findByTenantId(String tenantId);
}

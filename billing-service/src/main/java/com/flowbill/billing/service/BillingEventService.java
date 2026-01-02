package com.flowbill.billing.service;

import com.flowbill.billing.config.TenantContext;
import com.flowbill.billing.entity.BillingEvent;
import com.flowbill.billing.entity.BillingEntityType;
import com.flowbill.billing.entity.BillingEventType;
import com.flowbill.billing.repository.BillingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BillingEventService {

    @Autowired
    private BillingEventRepository eventRepository;

    @Autowired
    private TenantContext tenantContext;

    public void log(
            BillingEntityType entityType,
            Long entityId,
            BillingEventType eventType,
            Long actorUserId,
            Map<String, Object> beforeState,
            Map<String, Object> afterState) {
        BillingEvent event = new BillingEvent();
        event.setTenantId(tenantContext.getCurrentTenant());
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setEventType(eventType);
        event.setActorUserId(actorUserId);
        event.setBeforeState(beforeState);
        event.setAfterState(afterState);

        eventRepository.save(event);
    }
}

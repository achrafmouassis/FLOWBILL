package com.flowbill.billing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "billing_events")
@Getter
@Setter
public class BillingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private BillingEntityType entityType; // QUOTE, INVOICE

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private BillingEventType eventType; // CREATED, ACCEPTED, REJECTED, INVOICED, PAID

    @Column(name = "actor_user_id", nullable = false)
    private Long actorUserId;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Type(JsonBinaryType.class)
    @Column(name = "before_state", columnDefinition = "jsonb")
    private Map<String, Object> beforeState;

    @Type(JsonBinaryType.class)
    @Column(name = "after_state", columnDefinition = "jsonb")
    private Map<String, Object> afterState;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}

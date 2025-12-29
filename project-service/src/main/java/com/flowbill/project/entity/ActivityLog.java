package com.flowbill.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
@Data
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "event_type", nullable = false)
    private String eventType; // TASK_COMPLETED, SPRINT_STARTED, etc.

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Column(name = "entity_type")
    private String entityType; // TASK, SPRINT, STORY

    @Column(name = "entity_id")
    private Long entityId;

    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

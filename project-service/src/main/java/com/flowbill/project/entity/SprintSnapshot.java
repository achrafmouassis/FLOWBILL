package com.flowbill.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "sprint_daily_snapshots", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "sprint_id", "snapshot_date" })
})
@Data
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class SprintSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id", nullable = false)
    private Sprint sprint;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "remaining_sp")
    private Integer remainingSp;

    @Column(name = "completed_sp")
    private Integer completedSp;

    @Column(name = "remaining_tasks")
    private Integer remainingTasks;

    @Column(name = "completed_tasks")
    private Integer completedTasks;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
    }
}

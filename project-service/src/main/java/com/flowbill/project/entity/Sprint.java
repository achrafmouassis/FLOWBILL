package com.flowbill.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "sprints")
@Data
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String goal;

    @Column(name = "target_velocity")
    private Integer targetVelocity;

    @Enumerated(EnumType.STRING)
    private SprintStatus status; // PLANNED, ACTIVE, COMPLETED

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "sprint")
    private List<Task> tasks = new ArrayList<>();

    @Column(name = "tenant_id")
    private String tenantId;

    @PrePersist
    public void prePersist() {
        this.tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        if (this.status == null) {
            this.status = SprintStatus.PLANNED;
        }
    }

    public enum SprintStatus {
        PLANNED, ACTIVE, COMPLETED
    }
}

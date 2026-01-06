package com.flowbill.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "project_team_members")
@Data
public class ProjectTeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @com.fasterxml.jackson.annotation.JsonBackReference
    @lombok.ToString.Exclude
    private Project project;

    @Column(name = "user_id")
    private Long userId;

    private String role; // Backend, Frontend, Tech Lead, etc.

    @Column(name = "capacity_hours")
    private Integer capacityHours;

    @Column(name = "joined_at")
    private LocalDate joinedAt;

    @Column(name = "tenant_id")
    private String tenantId;

    @PrePersist
    public void prePersist() {
        this.tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        if (this.joinedAt == null) {
            this.joinedAt = LocalDate.now();
        }
    }
}

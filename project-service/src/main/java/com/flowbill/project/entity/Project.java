package com.flowbill.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "projects")
@Data
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String code;
    private String description;

    @Column(name = "description_detail")
    private String descriptionDetail;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private String type; // WEB, MOBILE, etc.

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "workflow_config")
    private String workflowConfig; // JSON string for Kanban columns

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<ProjectTeamMember> team = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ProjectStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ProjectStatus {
        ACTIVE, PLANNED, ON_HOLD, COMPLETED, ARCHIVED
    }
}

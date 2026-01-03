package com.flowbill.project.entity;

import jakarta.persistence.*;
import lombok.Data;

import com.flowbill.project.enums.MoscowPriority;
import com.flowbill.project.enums.TaskPriority;
import com.flowbill.project.enums.TaskStatus;
import com.flowbill.project.enums.TaskType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "tasks")
@Data
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Task {
    @Id
    private Long id;

    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    // MVP Additions
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TaskType type = TaskType.TASK;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private TaskPriority priority;

    // Agile / Backlog Management
    @Enumerated(EnumType.STRING)
    @Column(name = "moscow_priority")
    private MoscowPriority moscowPriority;

    @Column(name = "business_value")
    private Integer businessValue;

    @Column(name = "time_criticality")
    private Integer timeCriticality;

    @Column(name = "risk_reduction")
    private Integer riskReduction;

    @Column(name = "wsjf_score")
    private java.math.BigDecimal wsjfScore;

    @Column(name = "manual_order")
    private Integer manualOrder;

    private Integer estimation; // Story Points

    @Column(name = "due_date")
    private java.time.LocalDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    private Long assignedUserId; // ID from Auth Service

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_story_id")
    private Task parentStory;

    @OneToMany(mappedBy = "parentStory", cascade = CascadeType.ALL)
    private java.util.List<Task> subTasks = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<AcceptanceCriteria> acceptanceCriteria = new java.util.ArrayList<>();

    // Dependencies
    @OneToMany(mappedBy = "blocker", fetch = FetchType.LAZY)
    private java.util.List<TaskDependency> blocking = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "blocked", fetch = FetchType.LAZY)
    private java.util.List<TaskDependency> blockers = new java.util.ArrayList<>();

    @Column(name = "tenant_id")
    private String tenantId;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
        this.tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();

        // Ensure type default
        if (this.type == null) {
            this.type = TaskType.TASK;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}

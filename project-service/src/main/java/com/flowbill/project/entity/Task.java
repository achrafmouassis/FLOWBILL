package com.flowbill.project.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tasks")
@Data
public class Task {
    @Id
    private Long id;

    private String title;
    private String description;
    private String status; // TODO, IN_PROGRESS, DONE

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    // MVP Additions
    private String type = "TASK"; // STORY, TASK, BUG

    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

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

    @Column(name = "tenant_id")
    private String tenantId;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
        this.tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}

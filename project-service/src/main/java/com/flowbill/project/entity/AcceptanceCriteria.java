package com.flowbill.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "acceptance_criteria")
@Data
public class AcceptanceCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(name = "is_completed")
    private boolean isCompleted;

    @Column(name = "\"order\"")
    private Integer order;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

package com.flowbill.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "task_dependencies")
@Data
public class TaskDependency {

    @EmbeddedId
    private TaskDependencyId id;

    @ManyToOne
    @MapsId("blockerId")
    @JoinColumn(name = "blocker_id")
    private Task blocker;

    @ManyToOne
    @MapsId("blockedId")
    @JoinColumn(name = "blocked_id")
    private Task blocked;

    @Column(name = "dependency_type")
    private String dependencyType; // BLOCKING, RELATED

    @Embeddable
    @Data
    public static class TaskDependencyId implements Serializable {
        private Long blockerId;
        private Long blockedId;
    }
}

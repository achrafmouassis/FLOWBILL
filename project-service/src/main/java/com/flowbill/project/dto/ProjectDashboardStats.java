package com.flowbill.project.dto;

import lombok.Data;

@Data
public class ProjectDashboardStats {
    private long activeCount;
    private long plannedCount;
    private long onHoldCount;
    private long completedCount;
    private long totalTasks;
    private long teamSize; // Unique developers across projects
    private long activeSprints;
}

package com.flowbill.project.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprintDashboardResponse {
    private Long id;
    private String name;
    private String goal;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Long projectId;
    private String projectName;

    private Long totalDays;
    private Long daysRemaining;

    // Task Stats
    private int totalTasks;
    private int completedTasks;
    private int progressPercentage; // Renaming to match service usage if needed, or update service

    // SP Stats
    private int totalStoryPoints;
    private int completedStoryPoints;

    private String riskStatus; // GREEN, ORANGE, RED
}

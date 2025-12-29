package com.flowbill.project.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SprintDashboardResponse {
    private Long id;
    private String name;
    private String goal;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long projectId;
    private String projectName;

    // Calculated stats
    private long daysRemaining;
    private long totalDays;
    private int progressPercentage;

    private int totalTasks;
    private int completedTasks;

    private int totalStoryPoints;
    private int completedStoryPoints;

    private String riskStatus; // ON_TRACK, AT_RISK, AHEAD

    // Mini Burndown data could be complex, omitting for MVP step 1 or adding simple
    // list
    // private List<Integer> burndownData;
}

package com.flowbill.project.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalMetricsDTO {
    private long activeProjects;
    private long activeSprints;
    private long teamSize;
    private long activeTasks;
    private long backlogStories;
    private long tasksCompletedThisWeek;
}

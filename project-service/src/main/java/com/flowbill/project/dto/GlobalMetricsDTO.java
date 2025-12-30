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
    private long activeSprintsCount;
    private long teamSize;
    private TaskCounts activeTasks;
    private long backlogStories;
    private long tasksCompletedThisWeek;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskCounts {
        private long todo;
        private long inProgress;
        private long total;
    }
}

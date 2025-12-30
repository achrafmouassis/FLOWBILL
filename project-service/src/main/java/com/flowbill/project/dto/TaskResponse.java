package com.flowbill.project.dto;

import com.flowbill.project.entity.Task;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Integer estimation;
    private LocalDateTime dueDate;
    private Long projectId;
    private Long sprintId;
    private String sprintName;
    private Long assignedUserId;
    private String type;
    private Long parentStoryId;

    private String moscowPriority;
    private Integer businessValue;
    private Integer timeCriticality;
    private Integer riskReduction;
    private java.math.BigDecimal wsjfScore;
    private Integer manualOrder;

    // Dependencies
    private java.util.List<Long> blockingIds; // Tasks this task is blocking
    private java.util.List<Long> blockerIds; // Tasks blocking this task

    public static TaskResponse fromEntity(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setEstimation(task.getEstimation());
        response.setDueDate(task.getDueDate());
        response.setAssignedUserId(task.getAssignedUserId());
        response.setType(task.getType());

        // Agile Fields
        response.setMoscowPriority(task.getMoscowPriority());
        response.setBusinessValue(task.getBusinessValue());
        response.setTimeCriticality(task.getTimeCriticality());
        response.setRiskReduction(task.getRiskReduction());
        response.setWsjfScore(task.getWsjfScore());
        response.setManualOrder(task.getManualOrder());

        // Dependencies
        if (task.getBlocking() != null) {
            response.setBlockingIds(task.getBlocking().stream()
                    .map(td -> td.getBlocked().getId())
                    .collect(java.util.stream.Collectors.toList()));
        }
        if (task.getBlockers() != null) {
            response.setBlockerIds(task.getBlockers().stream()
                    .map(td -> td.getBlocker().getId())
                    .collect(java.util.stream.Collectors.toList()));
        }

        if (task.getParentStory() != null) {
            response.setParentStoryId(task.getParentStory().getId());
        }

        if (task.getProject() != null) {
            response.setProjectId(task.getProject().getId());
        }
        if (task.getSprint() != null) {
            response.setSprintId(task.getSprint().getId());
            response.setSprintName(task.getSprint().getName());
        }
        return response;
    }
}

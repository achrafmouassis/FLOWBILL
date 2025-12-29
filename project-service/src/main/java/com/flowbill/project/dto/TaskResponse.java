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

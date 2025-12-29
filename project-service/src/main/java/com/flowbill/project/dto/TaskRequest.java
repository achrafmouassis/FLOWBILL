package com.flowbill.project.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private String priority;
    private Integer estimation;
    private LocalDateTime dueDate;
    private Long projectId;
    private Long sprintId; // Optional, can be null for Backlog
    private Long assignedUserId;
    private String type; // STORY, TASK, BUG
    private Long parentStoryId;
    private String status; // TODO, IN_PROGRESS, DONE, BLOCKED
}

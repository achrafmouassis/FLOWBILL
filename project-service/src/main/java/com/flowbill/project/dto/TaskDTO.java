package com.flowbill.project.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Integer estimation;
    private String priority;
    private String type; // STORY, TASK, BUG

    // Relations enrichies
    private StoryBriefDTO parentStory;
    private SprintBriefDTO sprint;
    private ProjectBriefDTO project;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

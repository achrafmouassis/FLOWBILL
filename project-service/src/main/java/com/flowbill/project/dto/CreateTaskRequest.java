package com.flowbill.project.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateTaskRequest {
    @NotBlank
    @Size(min = 5, max = 255)
    private String title;

    private String description;

    @Min(1)
    private Integer estimationHours; // Or StoryPoints depending on strategy. Using hours/SP for tasks.

    private String competency;
}

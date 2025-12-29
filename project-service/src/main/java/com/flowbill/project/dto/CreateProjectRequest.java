package com.flowbill.project.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateProjectRequest {
    // Step 1: Infos
    private String name;
    private String code;
    private String description;
    private String descriptionDetail;
    private String type;
    private String clientName;

    // Step 2: Team
    private List<ProjectMemberDTO> teamMembers;

    // Step 3: Workflow
    private String workflowConfig; // JSON string

    // Step 4: Planning
    private LocalDate startDate;
    private LocalDate targetDate;
    private Integer sprintDurationWeeks;
}

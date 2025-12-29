package com.flowbill.project.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String descriptionDetail;
    private String status;
    private String type;
    private String clientName;
    private LocalDate startDate;
    private LocalDate targetDate;
    private String workflowConfig;
    private List<ProjectMemberDTO> teamMembers;
}

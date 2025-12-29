package com.flowbill.project.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProjectSummaryDTO {
    private Long id;
    private String name;
    private String code;
    private String clientName;
    private String status;
    private String type;
    private Double progress; // 0.0 to 1.0
    private LocalDate startDate;
    private LocalDate targetDate;
    private Integer activeSprintsCount;
    private Integer taskCount;
    private Integer completedTaskCount;
    private Integer teamSize;
}

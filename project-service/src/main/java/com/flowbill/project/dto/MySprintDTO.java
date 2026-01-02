package com.flowbill.project.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MySprintDTO {
    private Long id;
    private String name;
    private String status; // PLANNED, ACTIVE, COMPLETED
    private LocalDate startDate;
    private LocalDate endDate;
    private String goal;

    // Métriques personnelles du développeur
    private Integer myTotalTasks;
    private Integer myCompletedTasks;
    private Integer myCompletionRate; // Pourcentage (0-100)
    private Integer myRemainingHours;

    // Projet lié
    private ProjectBriefDTO project;
}

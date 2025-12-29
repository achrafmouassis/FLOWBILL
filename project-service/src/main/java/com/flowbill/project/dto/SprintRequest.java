package com.flowbill.project.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SprintRequest {
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String goal;
    private Long projectId;
}

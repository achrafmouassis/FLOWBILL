package com.flowbill.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SprintBriefDTO {
    private Long id;
    private String name;
    private LocalDate endDate;
    private Long daysRemaining;
}

package com.flowbill.project.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamLoadDTO {
    private Long userId;
    private String name;
    private int loadPercent;
    private String status; // AVAILABLE, LOADED, OVERLOADED
    private int assignedTaskCount;
}

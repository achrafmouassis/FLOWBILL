package com.flowbill.reporting.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class VelocityResponse {
    private String sprintName;
    private int committedPoints;
    private int completedPoints;
}

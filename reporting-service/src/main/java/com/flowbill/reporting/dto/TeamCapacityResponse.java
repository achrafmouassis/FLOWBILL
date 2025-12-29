package com.flowbill.reporting.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class TeamCapacityResponse {
    private Long userId; // Minimal proxy usage
    private String userName; // Placeholder or fetched
    private int totalAssignedPoints;
    private int activeTaskCount;
    private double capacityPercentage; // e.g. based on 40h or max points
}

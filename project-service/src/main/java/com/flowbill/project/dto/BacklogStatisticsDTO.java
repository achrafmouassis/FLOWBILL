package com.flowbill.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BacklogStatisticsDTO {
    private Integer totalStories;
    private Integer mustHaveCount;
    private Integer shouldHaveCount;
    private Integer couldHaveCount;
    private Integer wontHaveCount;
    private Integer totalStoryPoints;
    private Double avgWsjfScore;
    private Integer unplannedStories;
}

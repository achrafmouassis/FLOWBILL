package com.flowbill.project.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VelocityChartDTO {
    private List<String> sprints;
    private List<Integer> planned;
    private List<Integer> completed;
    private String trend; // STABLE, INCREASING, DECREASING
}

package com.flowbill.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BurndownChartDTO {
    private List<String> dates; // or Days
    private List<Integer> idealRemaining;
    private List<Integer> actualRemaining;
}

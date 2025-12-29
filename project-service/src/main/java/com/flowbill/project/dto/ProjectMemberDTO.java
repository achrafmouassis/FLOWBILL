package com.flowbill.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberDTO {
    private Long userId;
    private String fullName; // Filled from auth-service if needed
    private String role;
    private Integer capacityHours;
}

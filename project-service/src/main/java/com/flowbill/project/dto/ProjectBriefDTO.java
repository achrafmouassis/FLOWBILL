package com.flowbill.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectBriefDTO {
    private Long id;
    private String name;
    private String client;
}

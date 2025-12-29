package com.flowbill.project.dto;

import com.flowbill.project.entity.Sprint;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SprintResponse {
    private Long id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String goal;
    private Sprint.SprintStatus status;
    private Long projectId;

    public static SprintResponse fromEntity(Sprint sprint) {
        SprintResponse response = new SprintResponse();
        response.setId(sprint.getId());
        response.setName(sprint.getName());
        response.setStartDate(sprint.getStartDate());
        response.setEndDate(sprint.getEndDate());
        response.setGoal(sprint.getGoal());
        response.setStatus(sprint.getStatus());
        if (sprint.getProject() != null) {
            response.setProjectId(sprint.getProject().getId());
        }
        return response;
    }
}

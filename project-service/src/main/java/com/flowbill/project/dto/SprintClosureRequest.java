package com.flowbill.project.dto;

import lombok.Data;

@Data
public class SprintClosureRequest {
    private Long moveIncompleteToSprintId; // Null implies move to backlog
    private boolean moveToBacklog;
    // Add other closure details if needed (e.g., retrospective notes)
}

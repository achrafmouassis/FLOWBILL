package com.flowbill.project.dto;

import com.flowbill.project.entity.TaskDependency;
import lombok.Data;

@Data
public class TaskDependencyDTO {
    private Long blockerId;
    private String blockerTitle;
    private String blockerStatus;

    private Long blockedId;
    private String blockedTitle;
    private String blockedStatus;

    private String dependencyType;

    public static TaskDependencyDTO fromEntity(TaskDependency dep) {
        TaskDependencyDTO dto = new TaskDependencyDTO();
        dto.setBlockerId(dep.getBlocker().getId());
        dto.setBlockerTitle(dep.getBlocker().getTitle());
        dto.setBlockerStatus(dep.getBlocker().getStatus());

        dto.setBlockedId(dep.getBlocked().getId());
        dto.setBlockedTitle(dep.getBlocked().getTitle());
        dto.setBlockedStatus(dep.getBlocked().getStatus());

        dto.setDependencyType(dep.getDependencyType());
        return dto;
    }
}

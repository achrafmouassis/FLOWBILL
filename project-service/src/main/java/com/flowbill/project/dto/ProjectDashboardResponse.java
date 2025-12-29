package com.flowbill.project.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectDashboardResponse {
    private Long id;
    private String name;
    private String description; // Used as proxy for client if needed
    private String status; // ACTIVE, PAUSE, DONE

    private int completedTasks;
    private int totalTasks;
    private int progressPercentage;

    private String currentSprintName;
    private long sprintDaysRemaining;

    private List<TeamMemberDto> team;

    private String healthStatus; // GREEN, ORANGE, RED

    @Data
    public static class TeamMemberDto {
        private Long id;
        private String name;
        private String initials;

        public TeamMemberDto(Long id, String name) {
            this.id = id;
            this.name = name;
            this.initials = getInitialsFromName(name);
        }

        private String getInitialsFromName(String name) {
            if (name == null || name.isEmpty())
                return "?";
            String[] parts = name.split(" ");
            if (parts.length >= 2) {
                return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
            }
            return name.substring(0, Math.min(2, name.length())).toUpperCase();
        }
    }
}

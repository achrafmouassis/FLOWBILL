package com.flowbill.project.service;

import com.flowbill.project.entity.Project;
import com.flowbill.project.entity.ProjectTeamMember;
import com.flowbill.project.entity.Sprint;
import com.flowbill.project.entity.Task;
import com.flowbill.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing sprint capacity calculations and validation.
 */
@Service
@RequiredArgsConstructor
public class SprintCapacityService {

    private final TaskRepository taskRepository;

    /**
     * Calculates the total team capacity for a sprint based on team members.
     * 
     * @param project the project containing team members
     * @return total capacity hours, or null if no capacity is defined
     */
    public Integer calculateTotalCapacity(Project project) {
        if (project.getTeam() == null || project.getTeam().isEmpty()) {
            return null;
        }

        int totalCapacity = 0;
        boolean hasAnyCapacity = false;

        for (ProjectTeamMember member : project.getTeam()) {
            if (member.getCapacityHours() != null && member.getCapacityHours() > 0) {
                totalCapacity += member.getCapacityHours();
                hasAnyCapacity = true;
            }
        }

        return hasAnyCapacity ? totalCapacity : null;
    }

    /**
     * Calculates the total estimated hours already allocated in a sprint.
     * 
     * @param sprint   the sprint
     * @param tenantId the tenant ID
     * @return total estimated hours
     */
    public int calculateAllocatedHours(Sprint sprint, String tenantId) {
        Integer allocated = taskRepository.sumEstimationBySprintId(sprint.getId(), tenantId);
        return allocated != null ? allocated : 0;
    }

    /**
     * Calculates remaining capacity in a sprint.
     * 
     * @param sprint   the sprint
     * @param tenantId the tenant ID
     * @return remaining capacity hours, or null if no capacity limit is defined
     */
    public Integer calculateRemainingCapacity(Sprint sprint, String tenantId) {
        Integer totalCapacity = calculateTotalCapacity(sprint.getProject());
        if (totalCapacity == null) {
            return null; // No capacity limit
        }

        int allocated = calculateAllocatedHours(sprint, tenantId);
        return totalCapacity - allocated;
    }

    /**
     * Validates if adding a task to a sprint would exceed capacity.
     * 
     * @param sprint         the sprint
     * @param taskEstimation the estimation hours to add
     * @param tenantId       the tenant ID
     * @return true if capacity would be exceeded, false otherwise
     */
    public boolean wouldExceedCapacity(Sprint sprint, Integer taskEstimation, String tenantId) {
        Integer remaining = calculateRemainingCapacity(sprint, tenantId);

        // If no capacity is defined, always allow
        if (remaining == null) {
            return false;
        }

        // If task has no estimation, allow it (can be estimated later)
        if (taskEstimation == null || taskEstimation == 0) {
            return false;
        }

        return taskEstimation > remaining;
    }

    /**
     * Gets a detailed capacity report for a sprint.
     * 
     * @param sprint   the sprint
     * @param tenantId the tenant ID
     * @return capacity info DTO
     */
    public SprintCapacityInfo getCapacityInfo(Sprint sprint, String tenantId) {
        Integer totalCapacity = calculateTotalCapacity(sprint.getProject());
        int allocated = calculateAllocatedHours(sprint, tenantId);

        SprintCapacityInfo info = new SprintCapacityInfo();
        info.setTotalCapacity(totalCapacity);
        info.setAllocatedHours(allocated);
        info.setRemainingHours(totalCapacity != null ? totalCapacity - allocated : null);
        info.setHasCapacityLimit(totalCapacity != null);

        if (totalCapacity != null && totalCapacity > 0) {
            info.setUtilizationPercentage((int) ((allocated * 100.0) / totalCapacity));
        }

        return info;
    }

    /**
     * DTO for sprint capacity information.
     */
    public static class SprintCapacityInfo {
        private Integer totalCapacity;
        private Integer allocatedHours;
        private Integer remainingHours;
        private Integer utilizationPercentage;
        private Boolean hasCapacityLimit;

        public Integer getTotalCapacity() {
            return totalCapacity;
        }

        public void setTotalCapacity(Integer totalCapacity) {
            this.totalCapacity = totalCapacity;
        }

        public Integer getAllocatedHours() {
            return allocatedHours;
        }

        public void setAllocatedHours(Integer allocatedHours) {
            this.allocatedHours = allocatedHours;
        }

        public Integer getRemainingHours() {
            return remainingHours;
        }

        public void setRemainingHours(Integer remainingHours) {
            this.remainingHours = remainingHours;
        }

        public Integer getUtilizationPercentage() {
            return utilizationPercentage;
        }

        public void setUtilizationPercentage(Integer utilizationPercentage) {
            this.utilizationPercentage = utilizationPercentage;
        }

        public Boolean getHasCapacityLimit() {
            return hasCapacityLimit;
        }

        public void setHasCapacityLimit(Boolean hasCapacityLimit) {
            this.hasCapacityLimit = hasCapacityLimit;
        }
    }
}

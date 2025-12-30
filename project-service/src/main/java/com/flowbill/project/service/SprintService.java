package com.flowbill.project.service;

import com.flowbill.project.dto.SprintRequest;
import com.flowbill.project.dto.SprintResponse;
import com.flowbill.project.entity.Project;
import com.flowbill.project.entity.Sprint;
import com.flowbill.project.repository.ProjectRepository;
import com.flowbill.project.repository.SprintRepository;
import com.flowbill.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ActivityLogService activityLogService;
    private final BurndownService burndownService;

    @Transactional
    public SprintResponse createSprint(SprintRequest request) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        Project project = projectRepository.findByIdAndTenantId(request.getProjectId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found in your tenant"));

        Sprint sprint = new Sprint();
        sprint.setName(request.getName());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
        sprint.setGoal(request.getGoal());
        sprint.setProject(project);
        sprint.setStatus(Sprint.SprintStatus.PLANNED);

        Sprint savedSprint = sprintRepository.save(sprint);
        return SprintResponse.fromEntity(savedSprint);
    }

    @Transactional
    public SprintResponse startSprint(Long sprintId) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        Sprint sprint = sprintRepository.findByIdAndTenantId(sprintId, tenantId)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));

        if (sprint.getStatus() != Sprint.SprintStatus.PLANNED) {
            throw new RuntimeException("Sprint must be in PLANNED status to start");
        }

        // Ensure no other sprint is ACTIVE for this project?
        long activeCount = sprintRepository.findAll().stream()
                .filter(s -> s.getProject().getId().equals(sprint.getProject().getId())
                        && s.getStatus() == Sprint.SprintStatus.ACTIVE
                        && s.getTenantId().equals(tenantId))
                .count();

        if (activeCount > 0) {
            throw new RuntimeException("Another sprint is already active for this project. Complete it first.");
        }

        sprint.setStatus(Sprint.SprintStatus.ACTIVE);
        // Optional: Reset start date to today if starting late? Or keep planned?
        // Agile best practice: Start date is "now".
        // sprint.setStartDate(java.time.LocalDate.now());

        Sprint savedSprint = sprintRepository.save(sprint);

        // Create initial snapshot for Burndown
        burndownService.createInitialSnapshot(savedSprint.getId());

        activityLogService.logActivity("SPRINT_STARTED", "SPRINT", savedSprint.getId(),
                "Started sprint: " + savedSprint.getName());

        return SprintResponse.fromEntity(savedSprint);
    }

    @Transactional
    public void completeSprint(Long sprintId, com.flowbill.project.dto.SprintClosureRequest request) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        Sprint sprint = sprintRepository.findByIdAndTenantId(sprintId, tenantId)
                .orElseThrow(() -> new RuntimeException("Sprint not found in your tenant"));

        if (sprint.getStatus() != Sprint.SprintStatus.ACTIVE) {
            throw new RuntimeException("Only active sprints can be completed");
        }

        // Find incomplete tasks
        List<com.flowbill.project.entity.Task> incompleteTasks = taskRepository.findBySprintId(sprintId).stream()
                .filter(t -> !"DONE".equals(t.getStatus()))
                .collect(Collectors.toList());

        if (request.isMoveToBacklog() || request.getMoveIncompleteToSprintId() == null) {
            // Move to Backlog
            incompleteTasks.forEach(t -> {
                t.setSprint(null);
                taskRepository.save(t);
            });
        } else {
            // Move to target sprint
            Sprint targetSprint = sprintRepository.findByIdAndTenantId(request.getMoveIncompleteToSprintId(), tenantId)
                    .orElseThrow(() -> new RuntimeException("Target sprint not found in your tenant"));

            // Validation: Target sprint must belong to the same project
            if (!targetSprint.getProject().getId().equals(sprint.getProject().getId())) {
                throw new RuntimeException("Target sprint must belong to the same project");
            }

            // Validation: Target sprint must not be completed
            if (targetSprint.getStatus() == Sprint.SprintStatus.COMPLETED) {
                throw new RuntimeException("Cannot move tasks to a completed sprint");
            }

            incompleteTasks.forEach(t -> {
                t.setSprint(targetSprint);
                taskRepository.save(t);
            });
        }

        // Update Sprint Status
        sprint.setStatus(Sprint.SprintStatus.COMPLETED);
        sprint.setEndDate(LocalDateTime.now()); // Set actual end time
        sprintRepository.save(sprint);

        activityLogService.logActivity("SPRINT_COMPLETED", "SPRINT", sprint.getId(),
                "Completed sprint: " + sprint.getName());
    }

    public List<Sprint> getActiveSprints() {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        return sprintRepository.findAll().stream()
                .filter(s -> s.getStatus() == Sprint.SprintStatus.ACTIVE && tenantId.equals(s.getTenantId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public java.util.List<com.flowbill.project.dto.SprintDashboardResponse> getActiveSprintsWithStats() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<Sprint> activeSprints = sprintRepository.findAll().stream()
                .filter(s -> s.getStartDate().isBefore(now) && s.getEndDate().isAfter(now))
                .collect(Collectors.toList());

        return activeSprints.stream().map(sprint -> {
            com.flowbill.project.dto.SprintDashboardResponse resp = new com.flowbill.project.dto.SprintDashboardResponse();
            resp.setId(sprint.getId());
            resp.setName(sprint.getName());
            resp.setGoal(sprint.getGoal());
            resp.setStartDate(sprint.getStartDate());
            resp.setEndDate(sprint.getEndDate());
            if (sprint.getProject() != null) {
                resp.setProjectId(sprint.getProject().getId());
                resp.setProjectName(sprint.getProject().getName());
            }

            // Time calculation
            long totalDays = java.time.temporal.ChronoUnit.DAYS.between(sprint.getStartDate(), sprint.getEndDate());
            if (totalDays == 0)
                totalDays = 1;
            long daysElapsed = java.time.temporal.ChronoUnit.DAYS.between(sprint.getStartDate(), now);
            long daysRemaining = totalDays - daysElapsed;

            resp.setTotalDays(totalDays);
            resp.setDaysRemaining(daysRemaining < 0 ? 0 : daysRemaining);

            // Tasks stats
            List<com.flowbill.project.entity.Task> sprintTasks = taskRepository.findBySprintId(sprint.getId());
            int totalTasks = sprintTasks.size();
            int completedTasks = (int) sprintTasks.stream().filter(t -> "DONE".equals(t.getStatus())).count();

            resp.setTotalTasks(totalTasks);
            resp.setCompletedTasks(completedTasks);
            resp.setProgressPercentage(totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0);

            // Story Points
            // Assuming we have estimation field (Integer)
            int totalSP = sprintTasks.stream().mapToInt(t -> t.getEstimation() != null ? t.getEstimation() : 0).sum();
            int completedSP = sprintTasks.stream()
                    .filter(t -> "DONE".equals(t.getStatus()))
                    .mapToInt(t -> t.getEstimation() != null ? t.getEstimation() : 0).sum();

            resp.setTotalStoryPoints(totalSP);
            resp.setCompletedStoryPoints(completedSP);

            // Risk Status
            if (daysRemaining < 2 && resp.getProgressPercentage() < 80) {
                resp.setRiskStatus("RED");
            } else if (daysRemaining < 5 && resp.getProgressPercentage() < 50) {
                resp.setRiskStatus("ORANGE");
            } else {
                resp.setRiskStatus("GREEN");
            }

            return resp;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SprintResponse> getSprintsByProject(Long projectId) {
        return sprintRepository.findByProjectId(projectId).stream()
                .map(SprintResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

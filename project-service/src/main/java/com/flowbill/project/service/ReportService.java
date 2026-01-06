package com.flowbill.project.service;

import com.flowbill.project.dto.*;
import com.flowbill.project.repository.SprintRepository;
import com.flowbill.project.repository.TaskRepository;
import com.flowbill.project.entity.Task;
import com.flowbill.project.entity.Sprint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import com.flowbill.project.config.TenantContext;

@Service
@RequiredArgsConstructor
public class ReportService {

        private final SprintRepository sprintRepository;
        private final TaskRepository taskRepository;
        private final com.flowbill.project.repository.ProjectRepository projectRepository;

        // --- BLOC 1: Global Metrics ---
        public GlobalMetricsDTO getGlobalMetrics(Long projectId) {
                String tenantId = TenantContext.getCurrentTenant();

                if (projectId == null) {
                        // Tenant-wide Metrics
                        long activeProjects = projectRepository.countByTenantIdAndStatus(tenantId,
                                        com.flowbill.project.entity.Project.ProjectStatus.ACTIVE);
                        long activeSprints = sprintRepository.countByTenantIdAndStatus(tenantId,
                                        com.flowbill.project.entity.Sprint.SprintStatus.ACTIVE);

                        long todo = taskRepository.countByStatusAndTenantId(com.flowbill.project.enums.TaskStatus.TODO,
                                        tenantId);
                        long inProgress = taskRepository.countByStatusAndTenantId(
                                        com.flowbill.project.enums.TaskStatus.IN_PROGRESS, tenantId);
                        long totalActive = todo + inProgress;

                        long backlogStories = taskRepository.countByTypeAndStatusAndTenantId(
                                        com.flowbill.project.enums.TaskType.STORY,
                                        com.flowbill.project.enums.TaskStatus.TODO, tenantId);

                        // Approximate team size: distinct users in project teams for this tenant
                        long teamSize = projectRepository.findAllByTenantId(tenantId).stream()
                                        .flatMap(p -> p.getTeam().stream())
                                        .map(m -> m.getUserId())
                                        .distinct()
                                        .count();

                        // Tasks completed this week
                        java.time.LocalDateTime startOfWeek = java.time.LocalDateTime.now()
                                        .with(java.time.temporal.TemporalAdjusters
                                                        .previousOrSame(java.time.DayOfWeek.MONDAY))
                                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
                        long weeklyCompleted = taskRepository.countByStatusAndTenantIdAndUpdatedAtAfter(
                                        com.flowbill.project.enums.TaskStatus.DONE, tenantId, startOfWeek);

                        return GlobalMetricsDTO.builder()
                                        .activeProjects(activeProjects)
                                        .activeSprints(activeSprints)
                                        .activeTasks(totalActive)
                                        .backlogStories(backlogStories)
                                        .teamSize(teamSize)
                                        .tasksCompletedThisWeek(weeklyCompleted)
                                        .build();
                } else {
                        // Project-specific Metrics
                        long activeSprints = sprintRepository.countByProjectIdAndStatusAndTenantId(projectId,
                                        Sprint.SprintStatus.ACTIVE, tenantId);

                        List<Task> allTasks = taskRepository.findByProjectIdAndTenantId(projectId, tenantId);
                        long todo = allTasks.stream()
                                        .filter(t -> t.getStatus() == com.flowbill.project.enums.TaskStatus.TODO)
                                        .count();
                        long inProgress = allTasks.stream()
                                        .filter(t -> t.getStatus() == com.flowbill.project.enums.TaskStatus.IN_PROGRESS)
                                        .count();
                        long totalActive = todo + inProgress;

                        long backlogStories = taskRepository.countByProjectIdAndStatusAndTenantId(projectId,
                                        com.flowbill.project.enums.TaskStatus.TODO, tenantId);

                        // Project-specific team size and weekly completion
                        long teamSize = projectRepository.findByIdAndTenantId(projectId, tenantId)
                                        .map(p -> p.getTeam().size())
                                        .orElse(0);

                        java.time.LocalDateTime startOfWeek = java.time.LocalDateTime.now()
                                        .with(java.time.temporal.TemporalAdjusters
                                                        .previousOrSame(java.time.DayOfWeek.MONDAY))
                                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
                        long weeklyCompleted = taskRepository.findByProjectIdAndTenantId(projectId, tenantId).stream()
                                        .filter(t -> t.getStatus() == com.flowbill.project.enums.TaskStatus.DONE
                                                        && t.getUpdatedAt().isAfter(startOfWeek))
                                        .count();

                        return GlobalMetricsDTO.builder()
                                        .activeProjects(1L)
                                        .activeSprints(activeSprints)
                                        .activeTasks(totalActive)
                                        .backlogStories(backlogStories)
                                        .teamSize(teamSize)
                                        .tasksCompletedThisWeek(weeklyCompleted)
                                        .build();
                }
        }

        // --- BLOC 5: Verlocity Chart ---
        @Transactional(readOnly = true)
        public VelocityChartDTO getVelocityChart(Long projectId) {
                String tenantId = TenantContext.getCurrentTenant();

                // Get last 5 completed sprints for the project
                List<com.flowbill.project.entity.Sprint> completedSprints = sprintRepository
                                .findByProjectIdAndTenantId(projectId, tenantId)
                                .stream()
                                .filter(s -> s.getStatus() == com.flowbill.project.entity.Sprint.SprintStatus.COMPLETED)
                                .sorted((s1, s2) -> s2.getEndDate().compareTo(s1.getEndDate()))
                                .limit(5)
                                .collect(Collectors.toList());

                // Reverse to have chronological order in chart
                java.util.Collections.reverse(completedSprints);

                List<String> names = completedSprints.stream().map(com.flowbill.project.entity.Sprint::getName)
                                .collect(Collectors.toList());
                List<Integer> planned = completedSprints.stream()
                                .map(s -> taskRepository.sumEstimationBySprintId(s.getId(), tenantId))
                                .collect(Collectors.toList());
                List<Integer> completed = completedSprints.stream()
                                .map(s -> taskRepository.sumCompletedStoryPointsBySprintId(s.getId(), tenantId))
                                .collect(Collectors.toList());

                return VelocityChartDTO.builder()
                                .sprints(names)
                                .planned(planned)
                                .completed(completed)
                                .trend("STABLE") // Logic for trend could be added
                                .build();
        }

        @Transactional(readOnly = true)
        public List<ReportAlertDTO> getAlerts(Long projectId) {
                List<ReportAlertDTO> alerts = new ArrayList<>();
                String tenantId = TenantContext.getCurrentTenant();

                // 1. Alert: Active Sprints at risk
                List<com.flowbill.project.entity.Sprint> activeSprints;
                if (projectId != null) {
                        activeSprints = sprintRepository.findByProjectIdAndTenantId(projectId, tenantId);
                } else {
                        activeSprints = sprintRepository.findAllByTenantId(tenantId);
                }

                activeSprints = activeSprints.stream()
                                .filter(s -> s.getStatus() == com.flowbill.project.entity.Sprint.SprintStatus.ACTIVE)
                                .collect(Collectors.toList());

                LocalDateTime now = LocalDateTime.now();
                int alertCounter = 1;
                for (com.flowbill.project.entity.Sprint sprint : activeSprints) {
                        if (sprint.getEndDate().isBefore(now)) {
                                alerts.add(ReportAlertDTO.builder()
                                                .id("sprint_overdue_" + alertCounter++)
                                                .type("SPRINT_OVERDUE")
                                                .message("Le sprint " + sprint.getName() + " de "
                                                                + sprint.getProject().getName()
                                                                + " est en retard !")
                                                .severity("CRITICAL")
                                                .build());
                        } else {
                                long totalSP = taskRepository.sumEstimationBySprintId(sprint.getId(), tenantId);
                                long completedSP = taskRepository.sumCompletedStoryPointsBySprintId(sprint.getId(),
                                                tenantId);
                                if (totalSP > 0 && (completedSP * 100.0 / totalSP) < 20
                                                && ChronoUnit.DAYS.between(sprint.getStartDate(), now) > 3) {
                                        alerts.add(ReportAlertDTO.builder()
                                                        .id("low_progress_" + alertCounter++)
                                                        .type("LOW_PROGRESS")
                                                        .message("Faible progression sur le sprint " + sprint.getName()
                                                                        + " ("
                                                                        + sprint.getProject().getName() + ")")
                                                        .severity("WARNING")
                                                        .build());
                                }
                        }
                }

                // 2. Alert: Blocked tasks
                long blockedCount;
                if (projectId != null) {
                        blockedCount = taskRepository.findByProjectIdAndTenantId(projectId, tenantId).stream()
                                        .filter(t -> t.getStatus() == com.flowbill.project.enums.TaskStatus.BLOCKED)
                                        .count();
                } else {
                        blockedCount = taskRepository.countByStatusAndTenantId(
                                        com.flowbill.project.enums.TaskStatus.BLOCKED,
                                        tenantId);
                }

                if (blockedCount > 0) {
                        alerts.add(ReportAlertDTO.builder()
                                        .id("blocked_tasks_" + alertCounter++)
                                        .type("BLOCKED_TASKS")
                                        .message(blockedCount + " tâches sont actuellement bloquées")
                                        .severity("WARNING")
                                        .build());
                }

                return alerts;
        }

        // --- BLOC 6: Team Load ---
        public TeamLoadDTO getTeamLoad(Long projectId) {
                // Mocked as competency data is in auth-service, but could be implemented with
                // project_team_members
                return new TeamLoadDTO();
        }
}

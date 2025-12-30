package com.flowbill.project.service;

import com.flowbill.project.dto.*;
import com.flowbill.project.repository.ProjectRepository;
import com.flowbill.project.repository.SprintRepository;
import com.flowbill.project.repository.TaskRepository;
import com.flowbill.project.entity.Task;
import com.flowbill.project.entity.Sprint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import com.flowbill.project.config.TenantContext;

@Service
@RequiredArgsConstructor
public class ReportService {

        private final ProjectRepository projectRepository;
        private final SprintRepository sprintRepository;
        private final TaskRepository taskRepository;
        private final SprintService sprintService;

        // --- BLOC 1: Global Metrics ---
        public GlobalMetricsDTO getGlobalMetrics(Long projectId) {
                long activeProjects = 1; // For now single project scope or derived
                long activeSprints = sprintRepository.countByProjectIdAndStatus(projectId, Sprint.SprintStatus.ACTIVE);

                List<Task> allTasks = taskRepository.findByProjectId(projectId);
                long todo = allTasks.stream().filter(t -> "TODO".equals(t.getStatus())).count();
                long inProgress = allTasks.stream().filter(t -> "IN_PROGRESS".equals(t.getStatus())).count();
                long totalActive = todo + inProgress;

                long backlogStories = taskRepository.countByProjectIdAndStatus(projectId, "TODO"); // Needs refinement
                                                                                                   // for
                                                                                                   // Backlog
                                                                                                   // specifically

                return GlobalMetricsDTO.builder()
                                .activeProjects(activeProjects)
                                .activeSprintsCount(activeSprints)
                                .activeTasks(new GlobalMetricsDTO.TaskCounts(todo, inProgress, totalActive))
                                .backlogStories(backlogStories)
                                .build();
        }

        // --- BLOC 5: Verlocity Chart ---
        @Transactional(readOnly = true)
        public VelocityChartDTO getVelocityChart(Long projectId) {
                String tenantId = TenantContext.getCurrentTenant();

                // Get last 5 completed sprints for the project
                List<com.flowbill.project.entity.Sprint> completedSprints = sprintRepository.findByProjectId(projectId)
                                .stream()
                                .filter(s -> s.getStatus() == com.flowbill.project.entity.Sprint.SprintStatus.COMPLETED
                                                && tenantId.equals(s.getTenantId()))
                                .sorted((s1, s2) -> s2.getEndDate().compareTo(s1.getEndDate()))
                                .limit(5)
                                .collect(Collectors.toList());

                // Reverse to have chronological order in chart
                java.util.Collections.reverse(completedSprints);

                List<String> names = completedSprints.stream().map(com.flowbill.project.entity.Sprint::getName)
                                .collect(Collectors.toList());
                List<Integer> planned = completedSprints.stream()
                                .map(s -> taskRepository.sumEstimationBySprintId(s.getId()))
                                .collect(Collectors.toList());
                List<Integer> completed = completedSprints.stream()
                                .map(s -> taskRepository.sumCompletedStoryPointsBySprintId(s.getId()))
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

                // 1. Alert: Active Sprints at risk (Overdue or low progress)
                List<com.flowbill.project.entity.Sprint> activeSprints = sprintRepository.findByProjectId(projectId)
                                .stream()
                                .filter(s -> s.getStatus() == com.flowbill.project.entity.Sprint.SprintStatus.ACTIVE
                                                && tenantId.equals(s.getTenantId()))
                                .collect(Collectors.toList());

                LocalDateTime now = LocalDateTime.now();
                for (com.flowbill.project.entity.Sprint sprint : activeSprints) {
                        if (sprint.getEndDate().isBefore(now)) {
                                alerts.add(ReportAlertDTO.builder()
                                                .type("SPRINT_OVERDUE")
                                                .message("Le sprint " + sprint.getName() + " est en retard !")
                                                .severity("CRITICAL")
                                                .build());
                        } else {
                                long totalSP = taskRepository.sumEstimationBySprintId(sprint.getId());
                                long completedSP = taskRepository.sumCompletedStoryPointsBySprintId(sprint.getId());
                                if (totalSP > 0 && (completedSP * 100.0 / totalSP) < 20
                                                && ChronoUnit.DAYS.between(sprint.getStartDate(), now) > 3) {
                                        alerts.add(ReportAlertDTO.builder()
                                                        .type("LOW_PROGRESS")
                                                        .message("Faible progression sur le sprint " + sprint.getName())
                                                        .severity("WARNING")
                                                        .build());
                                }
                        }
                }

                // 2. Alert: Blocked tasks
                long blockedCount = taskRepository.findByProjectId(projectId).stream()
                                .filter(t -> "BLOCKED".equals(t.getStatus()) && tenantId.equals(t.getTenantId()))
                                .count();
                if (blockedCount > 0) {
                        alerts.add(ReportAlertDTO.builder()
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

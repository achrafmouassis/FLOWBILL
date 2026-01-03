package com.flowbill.project.service;

import com.flowbill.project.dto.TaskRequest;
import com.flowbill.project.dto.TaskResponse;
import com.flowbill.project.entity.Project;
import com.flowbill.project.entity.Sprint;
import com.flowbill.project.entity.Task;
import com.flowbill.project.entity.TaskHistory;
import com.flowbill.project.enums.MoscowPriority;
import com.flowbill.project.enums.TaskPriority;
import com.flowbill.project.enums.TaskStatus;
import com.flowbill.project.enums.TaskType;
import com.flowbill.project.dto.TaskDTO;
import com.flowbill.project.dto.StoryBriefDTO;
import com.flowbill.project.dto.SprintBriefDTO;
import com.flowbill.project.dto.ProjectBriefDTO;
import com.flowbill.project.repository.ProjectRepository;
import com.flowbill.project.repository.SprintRepository;
import com.flowbill.project.repository.TaskHistoryRepository;
import com.flowbill.project.repository.TaskRepository;
import com.flowbill.project.repository.TaskDependencyRepository;
import com.flowbill.project.exception.BadRequestException; // Assuming custom exception path
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final TaskDependencyRepository taskDependencyRepository;
    private final DependencyCycleDetector cycleDetector;
    private final ActivityLogService activityLogService;

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        Project project = projectRepository.findByIdAndTenantId(request.getProjectId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found in your tenant"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() != null ? TaskPriority.fromString(request.getPriority()) : null);
        task.setEstimation(request.getEstimation());
        task.setDueDate(request.getDueDate());
        task.setAssignedUserId(request.getAssignedUserId());
        task.setProject(project);

        // Initial Status defaults to TODO
        if (request.getStatus() != null) {
            task.setStatus(TaskStatus.fromString(request.getStatus()));
        }

        // MVP Additions
        if (request.getType() != null) {
            task.setType(TaskType.fromString(request.getType()));
        } else {
            task.setType(TaskType.TASK);
        }

        if (request.getParentStoryId() != null) {
            Task parent = taskRepository.findByIdAndTenantId(request.getParentStoryId(), tenantId)
                    .orElseThrow(() -> new RuntimeException("Parent Story not found in your tenant"));
            task.setParentStory(parent);
            if (!parent.getProject().getId().equals(project.getId())) {
                throw new RuntimeException("Subtask must belong to same project as parent story");
            }
        }

        if (request.getSprintId() != null) {
            Sprint sprint = sprintRepository.findByIdAndTenantId(request.getSprintId(), tenantId)
                    .orElseThrow(() -> new RuntimeException("Sprint not found in your tenant"));
            task.setSprint(sprint);
        }

        Task savedTask = taskRepository.save(task);

        // Log Activity
        activityLogService.logActivity("TASK_CREATED", "TASK", savedTask.getId(),
                "Created task: " + savedTask.getTitle());

        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new RuntimeException("Task not found in your tenant"));

        String oldStatus = task.getStatus().name(); // Get name for comparison

        if (request.getTitle() != null)
            task.setTitle(request.getTitle());
        if (request.getDescription() != null)
            task.setDescription(request.getDescription());
        if (request.getPriority() != null)
            task.setPriority(TaskPriority.fromString(request.getPriority()));
        if (request.getEstimation() != null)
            task.setEstimation(request.getEstimation());
        if (request.getDueDate() != null)
            task.setDueDate(request.getDueDate());
        if (request.getAssignedUserId() != null)
            task.setAssignedUserId(request.getAssignedUserId());

        if (request.getStatus() != null) {
            task.setStatus(TaskStatus.fromString(request.getStatus()));
        }
        // Update Sprint
        if (request.getSprintId() != null) {
            Sprint sprint = sprintRepository.findByIdAndTenantId(request.getSprintId(), tenantId)
                    .orElseThrow(() -> new RuntimeException("Sprint not found in your tenant"));
            task.setSprint(sprint);
        }

        Task savedTask = taskRepository.save(task);

        // History Tracking
        if (request.getStatus() != null && !request.getStatus().equals(oldStatus)) {
            TaskHistory history = new TaskHistory();
            history.setTask(savedTask);
            history.setFromStatus(oldStatus);
            history.setToStatus(request.getStatus());
            Long userId = com.flowbill.project.config.TenantContext.getCurrentUserId();
            history.setChangedByUserId(userId != null ? userId : 0L);
            taskHistoryRepository.save(history);

            // Log Activity
            if ("DONE".equals(request.getStatus())) {
                activityLogService.logActivity("TASK_COMPLETED", "TASK", savedTask.getId(),
                        "Completed task: " + savedTask.getTitle());
            } else {
                activityLogService.logActivity("TASK_UPDATED", "TASK", savedTask.getId(),
                        "Updated status to " + request.getStatus() + ": " + savedTask.getTitle());
            }
        }

        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksBySprint(Long sprintId) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        return taskRepository.findBySprintIdAndTenantId(sprintId, tenantId).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getBacklogTasks(Long projectId) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        return taskRepository.findByProjectIdAndSprintIsNullAndTenantId(projectId, tenantId).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getBacklogTasksForDeveloper(Long projectId, Long userId) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        // Only return stories where the developer has assigned tasks
        return taskRepository.findStoriesWithAssignedTasksForDeveloper(userId, tenantId).stream()
                .filter(t -> t.getProject().getId().equals(projectId))
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasksMulti(Long projectId) { // Just get all for project
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        return taskRepository.findByProjectIdAndTenantId(projectId, tenantId).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksForDeveloper(Long userId, List<String> statusFilters, Long sprintId, String sortBy) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        List<Task> tasks = taskRepository.findTasksForDeveloper(userId, statusFilters, sprintId, sortBy, tenantId);

        return tasks.stream().map(this::mapToTaskDTO).collect(Collectors.toList());
    }

    private TaskDTO mapToTaskDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus() != null ? task.getStatus().name() : null);
        dto.setEstimation(task.getEstimation());
        dto.setPriority(task.getPriority() != null ? task.getPriority().name() : null);
        dto.setType(task.getType() != null ? task.getType().name() : null);
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        if (task.getParentStory() != null) {
            dto.setParentStory(new StoryBriefDTO(
                    task.getParentStory().getId(),
                    task.getParentStory().getTitle(),
                    task.getParentStory().getEstimation()));
        }

        if (task.getSprint() != null) {
            long daysRemaining = 0;
            if (task.getSprint().getEndDate() != null) {
                daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(),
                        task.getSprint().getEndDate().toLocalDate());
            }
            dto.setSprint(new SprintBriefDTO(
                    task.getSprint().getId(),
                    task.getSprint().getName(),
                    task.getSprint().getEndDate() != null ? task.getSprint().getEndDate().toLocalDate() : null,
                    daysRemaining));
        }

        if (task.getProject() != null) {
            dto.setProject(new ProjectBriefDTO(
                    task.getProject().getId(),
                    task.getProject().getName(),
                    "Unknown Client" // Placeholder or fetch if Client entity exists/linked
            ));
        }

        return dto;
    }

    // --- Agile / Backlog Methods ---

    @Transactional
    public TaskResponse createStory(com.flowbill.project.dto.StoryCreationRequest request) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        Project project = projectRepository.findByIdAndTenantId(request.getProjectId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Task story = new Task();
        story.setType(TaskType.STORY);
        story.setTitle(request.getTitle());
        story.setDescription(request.getDescription());
        story.setEstimation(request.getEstimation());
        story.setPriority(TaskPriority.MEDIUM); // Default technical priority
        story.setStatus(TaskStatus.TODO);
        story.setProject(project);

        // Agile Fields
        story.setMoscowPriority(
                request.getMoscowPriority() != null ? MoscowPriority.fromString(request.getMoscowPriority()) : null);
        story.setBusinessValue(request.getBusinessValue());
        story.setTimeCriticality(request.getTimeCriticality());
        story.setRiskReduction(request.getRiskReduction());

        // Calculate WSJF
        calculateWsjf(story);

        // Sprint
        if (request.getSprintId() != null) {
            Sprint sprint = sprintRepository.findByIdAndTenantId(request.getSprintId(), tenantId)
                    .orElse(null);
            story.setSprint(sprint);
        }

        // Competencies / Acceptance Criteria could be handled here if entities exist
        // For MVP, if we have a JSON column or separate table for acceptance criteria:
        // (Assuming AcceptanceCriteria entity exists and is linked)

        Task savedStory = taskRepository.save(story);

        // Create Acceptance Criteria
        if (request.getAcceptanceCriteria() != null) {
            for (String desc : request.getAcceptanceCriteria()) {
                com.flowbill.project.entity.AcceptanceCriteria ac = new com.flowbill.project.entity.AcceptanceCriteria();
                ac.setDescription(desc);
                ac.setTask(savedStory);
                // save AC via repository or cascade if properly set
                // Assuming CascadeType.ALL on valid relationship inside Task
                savedStory.getAcceptanceCriteria().add(ac);
            }
            // Save again to persist children if cascade is on, or save children manually
            // For safety with JPA:
            // acceptanceCriteriaRepository.saveAll(...)
            // But let's assume Cascade work or we do a simple re-save
        }

        // Dependencies
        if (request.getBlockerIds() != null)

        {
            for (Long blockerId : request.getBlockerIds()) {
                try {
                    addDependency(savedStory.getId(), blockerId, "BLOCKING");
                } catch (Exception e) {
                    // Log error but don't fail story creation? Or fail?
                    // For MVP, ignore invalid blockers to keep story created
                }
            }
        }

        return TaskResponse.fromEntity(savedStory);
    }

    @Transactional
    public void decomposeStory(Long storyId, List<TaskRequest> subTasksRequests) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        Task story = taskRepository.findByIdAndTenantId(storyId, tenantId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

        if (story.getType() != TaskType.STORY) {
            throw new RuntimeException("Target task is not a Story");
        }

        for (TaskRequest req : subTasksRequests) {
            req.setParentStoryId(story.getId());
            req.setProjectId(story.getProject().getId());
            if (req.getSprintId() == null && story.getSprint() != null) {
                req.setSprintId(story.getSprint().getId());
            }
            createTask(req);
        }
    }

    private void calculateWsjf(Task task) {
        if (task.getBusinessValue() != null && task.getTimeCriticality() != null
                && task.getRiskReduction() != null && task.getEstimation() != null && task.getEstimation() > 0) {

            double costOfDelay = task.getBusinessValue() + task.getTimeCriticality() + task.getRiskReduction();
            double wsjf = costOfDelay / task.getEstimation();
            task.setWsjfScore(java.math.BigDecimal.valueOf(wsjf));
        } else {
            task.setWsjfScore(null);
        }
    }

    // --- Dependencies ---

    @Transactional
    public void addDependency(Long blockedId, Long blockerId, String type) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();

        // Check for dependency cycle BEFORE adding
        if (cycleDetector.wouldCreateCycle(blockedId, blockerId, tenantId)) {
            List<Long> cycle = cycleDetector.findCyclePath(blockedId, tenantId);
            throw new BadRequestException(
                    String.format("Cannot add dependency: would create a cycle. " +
                            "Detected cycle path: %s → %s", cycle, blockerId));
        }

        Task blocked = taskRepository.findByIdAndTenantId(blockedId, tenantId)
                .orElseThrow(() -> new RuntimeException("Blocked task not found"));
        Task blocker = taskRepository.findByIdAndTenantId(blockerId, tenantId)
                .orElseThrow(() -> new RuntimeException("Blocker task not found"));

        if (blocked.getId().equals(blocker.getId())) {
            throw new RuntimeException("Task cannot depend on itself");
        }

        // Circular check (Simple 1-level for now, or exhaustive DFS)
        // For MVP, just checking if blocked is already blocking blocker
        // Ideally should check full graph.

        com.flowbill.project.entity.TaskDependency dependency = new com.flowbill.project.entity.TaskDependency();
        com.flowbill.project.entity.TaskDependency.TaskDependencyId id = new com.flowbill.project.entity.TaskDependency.TaskDependencyId();
        id.setBlockedId(blocked.getId());
        id.setBlockerId(blocker.getId());
        dependency.setId(id);
        dependency.setBlocked(blocked);
        dependency.setBlocker(blocker);
        dependency.setDependencyType(type != null ? type : "BLOCKING");

        // We need a TaskDependencyRepository injected or use entityManager
        // Since we didn't inject it yet, let's assume we add it to the service
        // constructor
        taskDependencyRepository.save(dependency);
    }

    @Transactional
    public void removeDependency(Long blockedId, Long blockerId) {
        com.flowbill.project.entity.TaskDependency.TaskDependencyId id = new com.flowbill.project.entity.TaskDependency.TaskDependencyId();
        id.setBlockedId(blockedId);
        id.setBlockerId(blockerId);
        taskDependencyRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public com.flowbill.project.dto.BacklogStatisticsDTO calculateBacklogStatistics(Long projectId) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        long totalStories = taskRepository.countByProjectIdAndTypeAndTenantId(projectId,
                com.flowbill.project.enums.TaskType.STORY, tenantId);
        long mustHave = taskRepository.countByProjectIdAndTypeAndMoscowPriorityAndTenantId(projectId,
                com.flowbill.project.enums.TaskType.STORY,
                com.flowbill.project.enums.MoscowPriority.MUST_HAVE, tenantId);
        long shouldHave = taskRepository.countByProjectIdAndTypeAndMoscowPriorityAndTenantId(projectId,
                com.flowbill.project.enums.TaskType.STORY,
                com.flowbill.project.enums.MoscowPriority.SHOULD_HAVE, tenantId);
        long couldHave = taskRepository.countByProjectIdAndTypeAndMoscowPriorityAndTenantId(projectId,
                com.flowbill.project.enums.TaskType.STORY,
                com.flowbill.project.enums.MoscowPriority.COULD_HAVE, tenantId);
        long wontHave = taskRepository.countByProjectIdAndTypeAndMoscowPriorityAndTenantId(projectId,
                com.flowbill.project.enums.TaskType.STORY,
                com.flowbill.project.enums.MoscowPriority.WONT_HAVE, tenantId);
        Long totalSP = taskRepository.sumEstimationByProjectId(projectId, tenantId);
        Double avgWsjf = taskRepository.avgWsjfScoreByProjectId(projectId, tenantId);
        Long unplanned = taskRepository.countUnplannedStoriesByProjectId(projectId, tenantId);

        return com.flowbill.project.dto.BacklogStatisticsDTO.builder()
                .totalStories((int) totalStories)
                .mustHaveCount((int) mustHave)
                .shouldHaveCount((int) shouldHave)
                .couldHaveCount((int) couldHave)
                .wontHaveCount((int) wontHave)
                .totalStoryPoints(totalSP != null ? totalSP.intValue() : 0)
                .avgWsjfScore(avgWsjf)
                .unplannedStories(unplanned != null ? unplanned.intValue() : 0)
                .build();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId, Long userId, boolean isDev) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (isDev && !userId.equals(task.getAssignedUserId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not allowed to view this task");
        }

        return TaskResponse.fromEntity(task);
    }
}

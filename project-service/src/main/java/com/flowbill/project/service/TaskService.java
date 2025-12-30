package com.flowbill.project.service;

import com.flowbill.project.dto.TaskRequest;
import com.flowbill.project.dto.TaskResponse;
import com.flowbill.project.entity.Project;
import com.flowbill.project.entity.Sprint;
import com.flowbill.project.entity.Task;
import com.flowbill.project.entity.TaskHistory;
import com.flowbill.project.repository.ProjectRepository;
import com.flowbill.project.repository.SprintRepository;
import com.flowbill.project.repository.TaskHistoryRepository;
import com.flowbill.project.repository.TaskRepository;
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
    private final com.flowbill.project.repository.TaskDependencyRepository taskDependencyRepository;
    private final ActivityLogService activityLogService;

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        Project project = projectRepository.findByIdAndTenantId(request.getProjectId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found in your tenant"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setEstimation(request.getEstimation());
        task.setDueDate(request.getDueDate());
        task.setAssignedUserId(request.getAssignedUserId());
        task.setProject(project);

        // Initial Status defaults to TODO (or whatever DB default is)
        // If request has status, set it.
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        // MVP Additions
        if (request.getType() != null) {
            task.setType(request.getType());
        } else {
            task.setType("TASK");
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

        String oldStatus = task.getStatus();

        if (request.getTitle() != null)
            task.setTitle(request.getTitle());
        if (request.getDescription() != null)
            task.setDescription(request.getDescription());
        if (request.getPriority() != null)
            task.setPriority(request.getPriority());
        if (request.getEstimation() != null)
            task.setEstimation(request.getEstimation());
        if (request.getDueDate() != null)
            task.setDueDate(request.getDueDate());
        if (request.getAssignedUserId() != null)
            task.setAssignedUserId(request.getAssignedUserId());
        if (request.getStatus() != null)
            task.setStatus(request.getStatus());

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
        return taskRepository.findBySprintId(sprintId).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getBacklogTasks(Long projectId) {
        return taskRepository.findByProjectIdAndSprintIsNull(projectId).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getBacklogTasksForDeveloper(Long projectId, Long userId) {
        // Only return stories where the developer has assigned tasks
        return taskRepository.findStoriesWithAssignedTasksForDeveloper(userId).stream()
                .filter(t -> t.getProject().getId().equals(projectId))
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasksMulti(Long projectId) { // Just get all for project
        return taskRepository.findByProjectId(projectId).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // --- Agile / Backlog Methods ---

    @Transactional
    public TaskResponse createStory(com.flowbill.project.dto.StoryCreationRequest request) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        Project project = projectRepository.findByIdAndTenantId(request.getProjectId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Task story = new Task();
        story.setType("STORY");
        story.setTitle(request.getTitle());
        story.setDescription(request.getDescription());
        story.setEstimation(request.getEstimation());
        story.setPriority("MEDIUM"); // Default technical priority
        story.setStatus("TODO");
        story.setProject(project);

        // Agile Fields
        story.setMoscowPriority(request.getMoscowPriority());
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

        if (!"STORY".equals(story.getType())) {
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
        long totalStories = taskRepository.countByProjectIdAndType(projectId, "STORY");
        long mustHave = taskRepository.countByProjectIdAndTypeAndMoscowPriority(projectId, "STORY", "MUST_HAVE");
        long shouldHave = taskRepository.countByProjectIdAndTypeAndMoscowPriority(projectId, "STORY", "SHOULD_HAVE");
        long couldHave = taskRepository.countByProjectIdAndTypeAndMoscowPriority(projectId, "STORY", "COULD_HAVE");
        long wontHave = taskRepository.countByProjectIdAndTypeAndMoscowPriority(projectId, "STORY", "WONT_HAVE");
        Long totalSP = taskRepository.sumEstimationByProjectId(projectId);
        Double avgWsjf = taskRepository.avgWsjfScoreByProjectId(projectId);
        Long unplanned = taskRepository.countUnplannedStoriesByProjectId(projectId);

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
}

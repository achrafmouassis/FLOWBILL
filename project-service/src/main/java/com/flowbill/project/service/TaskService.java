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

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasksMulti(Long projectId) { // Just get all for project
        return taskRepository.findByProjectId(projectId).stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

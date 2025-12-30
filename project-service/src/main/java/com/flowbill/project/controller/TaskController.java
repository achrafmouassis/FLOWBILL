package com.flowbill.project.controller;

import com.flowbill.project.dto.TaskRequest;
import com.flowbill.project.dto.TaskResponse;
import com.flowbill.project.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(@RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long sprintId) {

        // MVP Security: If Developer, strictly filter by assignedUserId?
        // Actually, in Agile, developers see the whole board usually.
        // But the requirements said: "Les développeurs ne voient QUE leurs tâches
        // assignées" (Strict mode for this specific client requirement)

        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isDev = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        List<TaskResponse> tasks;

        if (sprintId != null) {
            tasks = taskService.getTasksBySprint(sprintId);
        } else if (projectId != null) {
            tasks = taskService.getAllTasksMulti(projectId);
        } else {
            return ResponseEntity.badRequest().build();
        }

        if (isDev) {
            Long currentUserId = 0L;
            if (auth.getDetails() instanceof Long) {
                currentUserId = (Long) auth.getDetails();
            } else {
                // Fallback: Try to parse header if details not set (should be set by filter
                // now)
                // Or just deny access/return empty if ID missing
                // For MVP, if we can't identify user, they see nothing or everything? Safety:
                // Nothing.
                return ResponseEntity.ok(List.of());
            }

            final Long finalUserId = currentUserId;
            tasks = tasks.stream()
                    .filter(t -> finalUserId.equals(t.getAssignedUserId()))
                    .collect(java.util.stream.Collectors.toList());
        }

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/backlog")
    public ResponseEntity<List<TaskResponse>> getBacklog(@RequestParam Long projectId) {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isDev = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        if (isDev) {
            Long currentUserId = 0L;
            if (auth.getDetails() instanceof Long) {
                currentUserId = (Long) auth.getDetails();
            } else {
                return ResponseEntity.ok(List.of());
            }
            return ResponseEntity.ok(taskService.getBacklogTasksForDeveloper(projectId, currentUserId));
        }

        return ResponseEntity.ok(taskService.getBacklogTasks(projectId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId, @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    @PostMapping("/{taskId}/dependencies")
    public ResponseEntity<Void> addDependency(@PathVariable Long taskId, @RequestParam Long blockerId) {
        taskService.addDependency(taskId, blockerId, "BLOCKING");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}/dependencies/{blockerId}")
    public ResponseEntity<Void> removeDependency(@PathVariable Long taskId, @PathVariable Long blockerId) {
        taskService.removeDependency(taskId, blockerId);
        return ResponseEntity.ok().build();
    }
}

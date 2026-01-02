package com.flowbill.project.service;

import com.flowbill.project.entity.Task;
import com.flowbill.project.entity.TaskDependency;
import com.flowbill.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service for detecting circular dependencies in task relationships.
 * Uses Depth-First Search (DFS) algorithm to find cycles.
 */
@Service
@RequiredArgsConstructor
public class DependencyCycleDetector {

    private final TaskRepository taskRepository;

    /**
     * Checks if adding a new dependency would create a cycle.
     * 
     * @param blockedTaskId the blocked task ID (depends on blocker)
     * @param blockerTaskId the blocker task ID
     * @param tenantId      the tenant ID
     * @return true if a cycle would be created, false otherwise
     */
    public boolean wouldCreateCycle(Long blockedTaskId, Long blockerTaskId, String tenantId) {
        // If blocked = blocker, that's a self-loop (cycle)
        if (blockedTaskId.equals(blockerTaskId)) {
            return true;
        }

        // Check if blocker depends on blocked (direct or transitive)
        // If yes, adding blocked -> blocker would create a cycle
        return hasPathDFS(blockerTaskId, blockedTaskId, tenantId);
    }

    /**
     * Checks if there's a path from 'start' to 'target' using DFS.
     * If a path exists from blocker to blocked, then adding blocked->blocker
     * creates a cycle.
     * 
     * @param start    starting task ID
     * @param target   target task ID
     * @param tenantId tenant ID
     * @return true if path exists
     */
    private boolean hasPathDFS(Long start, Long target, String tenantId) {
        Set<Long> visited = new HashSet<>();
        return dfs(start, target, visited, tenantId);
    }

    /**
     * Recursive DFS to find path from current to target.
     * 
     * @param current  current task ID
     * @param target   target task ID
     * @param visited  set of visited task IDs (to prevent infinite loops)
     * @param tenantId tenant ID
     * @return true if path found
     */
    private boolean dfs(Long current, Long target, Set<Long> visited, String tenantId) {
        // Base case: found the target
        if (current.equals(target)) {
            return true;
        }

        // Mark as visited to prevent revisiting
        if (visited.contains(current)) {
            return false;
        }
        visited.add(current);

        // Get all blockers of the current task (tasks that current depends on)
        List<Task> blockers = taskRepository.findBlockersForTask(current, tenantId);

        // Recursively search through each blocker
        for (Task blocker : blockers) {
            if (dfs(blocker.getId(), target, visited, tenantId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Finds all tasks in a dependency cycle starting from a task.
     * Useful for debugging/reporting.
     * 
     * @param taskId   the starting task ID
     * @param tenantId tenant ID
     * @return list of task IDs in the cycle, or empty if no cycle
     */
    public List<Long> findCyclePath(Long taskId, String tenantId) {
        Set<Long> visited = new HashSet<>();
        Set<Long> recursionStack = new HashSet<>();
        java.util.List<Long> path = new java.util.ArrayList<>();

        if (detectCycleDFS(taskId, visited, recursionStack, path, tenantId)) {
            return path;
        }

        return java.util.Collections.emptyList();
    }

    /**
     * DFS for cycle detection with path tracking.
     */
    private boolean detectCycleDFS(Long taskId, Set<Long> visited, Set<Long> recursionStack,
            java.util.List<Long> path, String tenantId) {
        visited.add(taskId);
        recursionStack.add(taskId);
        path.add(taskId);

        List<Task> blockers = taskRepository.findBlockersForTask(taskId, tenantId);

        for (Task blocker : blockers) {
            Long blockerId = blocker.getId();

            if (!visited.contains(blockerId)) {
                if (detectCycleDFS(blockerId, visited, recursionStack, path, tenantId)) {
                    return true;
                }
            } else if (recursionStack.contains(blockerId)) {
                // Cycle detected!
                path.add(blockerId); // Add the repeated node to show cycle
                return true;
            }
        }

        recursionStack.remove(taskId);
        path.remove(path.size() - 1); // Backtrack
        return false;
    }
}

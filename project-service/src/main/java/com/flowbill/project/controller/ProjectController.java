package com.flowbill.project.controller;

import com.flowbill.project.dto.*;
import com.flowbill.project.entity.Project;
import com.flowbill.project.entity.Task;
import com.flowbill.project.repository.ProjectRepository;
import com.flowbill.project.repository.TaskRepository;
import com.flowbill.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @GetMapping("/search")
    public List<ProjectSummaryDTO> searchProjects(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        return projectService.searchProjects(query, status, type);
    }

    @GetMapping("/stats")
    public ProjectDashboardStats getStats() {
        return projectService.getProjectDashboardStats();
    }

    @GetMapping("/{id}")
    public ProjectDTO getProject(@PathVariable Long id) {
        return projectService.getProjectDetail(id);
    }

    @PostMapping("/wizard")
    @PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
    public ProjectDTO createProjectWizard(@RequestBody CreateProjectRequest request) {
        return projectService.createProjectFromWizard(request);
    }

    // --- Legacy / Compatibility Endpoints ---

    @GetMapping
    public List<Project> getProjectsLegacy() {
        return projectService.getAllProjects();
    }

    @GetMapping("/active-dashboard")
    @PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
    public List<ProjectDashboardResponse> getActiveProjectsDashboard() {
        return projectService.getActiveProjectsStats();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
    public Project createProjectLegacy(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    @PostMapping("/{projectId}/tasks")
    public Task createTask(@PathVariable Long projectId, @RequestBody Task task) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        Project p = projectRepository.findByIdAndTenantId(projectId, tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found in your tenant"));
        task.setProject(p);
        return taskRepository.save(task);
    }

    @GetMapping("/tasks/{taskId}")
    public Task getTask(@PathVariable Long taskId) {
        String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
        return taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new RuntimeException("Task not found in your tenant"));
    }
}

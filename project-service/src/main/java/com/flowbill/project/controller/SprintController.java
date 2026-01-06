package com.flowbill.project.controller;

import com.flowbill.project.dto.SprintRequest;
import com.flowbill.project.dto.SprintResponse;
import com.flowbill.project.dto.MySprintDTO;
import com.flowbill.project.dto.TaskResponse;
import com.flowbill.project.service.SprintService;
import com.flowbill.project.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;
    private final TaskService taskService;
    private final com.flowbill.project.service.BurndownService burndownService;

    @GetMapping("/sprints/active-dashboard")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
    public ResponseEntity<List<com.flowbill.project.dto.SprintDashboardResponse>> getActiveSprints() {
        return ResponseEntity.ok(sprintService.getActiveSprintsWithStats());
    }

    @GetMapping("/my-sprints")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<MySprintDTO>> getMySprints() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = 0L;
        if (auth.getDetails() instanceof Long) {
            currentUserId = (Long) auth.getDetails();
        }
        return ResponseEntity.ok(sprintService.getSprintsForDeveloper(currentUserId));
    }

    @GetMapping("/{id}/burndown")
    public ResponseEntity<com.flowbill.project.dto.BurndownChartDTO> getBurndownChart(@PathVariable Long id) {
        return ResponseEntity.ok(burndownService.getBurndownChart(id));
    }

    @PostMapping("/{sprintId}/complete")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
    public ResponseEntity<Void> completeSprint(@PathVariable Long sprintId,
            @RequestBody com.flowbill.project.dto.SprintClosureRequest request) {
        sprintService.completeSprint(sprintId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sprintId}/start")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
    public ResponseEntity<SprintResponse> startSprint(@PathVariable Long sprintId) {
        return ResponseEntity.ok(sprintService.startSprint(sprintId));
    }

    @PostMapping("/projects/{projectId}/sprints")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
    public ResponseEntity<SprintResponse> createSprint(@PathVariable Long projectId,
            @RequestBody SprintRequest request) {
        request.setProjectId(projectId);
        return ResponseEntity.ok(sprintService.createSprint(request));
    }

    @GetMapping("/projects/{projectId}/sprints")
    public ResponseEntity<List<SprintResponse>> getSprints(@PathVariable Long projectId) {
        return ResponseEntity.ok(sprintService.getSprintsByProject(projectId));
    }

    @GetMapping("/projects/{projectId}/sprints/{sprintId}/tasks")
    public ResponseEntity<List<TaskResponse>> getSprintTasks(@PathVariable Long projectId,
            @PathVariable Long sprintId) {
        return ResponseEntity.ok(taskService.getTasksBySprint(sprintId));
    }
}

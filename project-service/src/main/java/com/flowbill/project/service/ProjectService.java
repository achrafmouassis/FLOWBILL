package com.flowbill.project.service;

import com.flowbill.project.config.TenantContext;
import com.flowbill.project.dto.*;
import com.flowbill.project.entity.Project;
import com.flowbill.project.entity.ProjectTeamMember;
import com.flowbill.project.entity.Sprint;
import com.flowbill.project.repository.ProjectRepository;
import com.flowbill.project.repository.SprintRepository;
import com.flowbill.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final SprintRepository sprintRepository;
    private final UserService userService;

    @Transactional
    public ProjectDTO createProjectFromWizard(CreateProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setCode(request.getCode());
        project.setDescription(request.getDescription());
        project.setDescriptionDetail(request.getDescriptionDetail());
        project.setType(request.getType());
        project.setClientName(request.getClientName());
        project.setStartDate(request.getStartDate());
        project.setTargetDate(request.getTargetDate());
        project.setWorkflowConfig(request.getWorkflowConfig());
        project.setStatus(Project.ProjectStatus.ACTIVE);

        // Add team members
        if (request.getTeamMembers() != null) {
            for (ProjectMemberDTO memberDto : request.getTeamMembers()) {
                ProjectTeamMember member = new ProjectTeamMember();
                member.setProject(project);
                member.setUserId(memberDto.getUserId());
                member.setRole(memberDto.getRole());
                member.setCapacityHours(memberDto.getCapacityHours());
                project.getTeam().add(member);
            }
        }

        project = projectRepository.save(project);
        return mapToDTO(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryDTO> searchProjects(String query, String status, String type) {
        String tenantId = TenantContext.getCurrentTenant();
        Project.ProjectStatus projectStatus = status != null ? Project.ProjectStatus.valueOf(status) : null;

        List<Project> projects = projectRepository.searchProjects(tenantId, projectStatus, type, query);

        return projects.stream().map(this::mapToSummaryDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectDashboardStats getProjectDashboardStats() {
        String tenantId = TenantContext.getCurrentTenant();
        List<Project> projects = projectRepository.findAllByTenantId(tenantId);

        ProjectDashboardStats stats = new ProjectDashboardStats();
        stats.setActiveCount(projects.stream().filter(p -> p.getStatus() == Project.ProjectStatus.ACTIVE).count());
        stats.setPlannedCount(projects.stream().filter(p -> p.getStatus() == Project.ProjectStatus.PLANNED).count());
        stats.setOnHoldCount(projects.stream().filter(p -> p.getStatus() == Project.ProjectStatus.ON_HOLD).count());
        stats.setCompletedCount(
                projects.stream().filter(p -> p.getStatus() == Project.ProjectStatus.COMPLETED).count());

        stats.setTotalTasks(taskRepository.countByTenantId(tenantId));
        stats.setActiveSprints((int) sprintRepository.countByTenantIdAndStatus(tenantId, Sprint.SprintStatus.ACTIVE));

        // teamSize would be distinct user_id in project_team_members for this tenant
        // For MVP, we can approximate or do a custom query
        stats.setTeamSize(
                projects.stream().flatMap(p -> p.getTeam().stream()).map(m -> m.getUserId()).distinct().count());

        return stats;
    }

    @Transactional(readOnly = true)
    public ProjectDTO getProjectDetail(Long id) {
        String tenantId = TenantContext.getCurrentTenant();
        Project project = projectRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found in your tenant"));
        return mapToDTO(project);
    }

    private ProjectDTO mapToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setCode(project.getCode());
        dto.setDescription(project.getDescription());
        dto.setDescriptionDetail(project.getDescriptionDetail());
        dto.setStatus(project.getStatus().name());
        dto.setType(project.getType());
        dto.setClientName(project.getClientName());
        dto.setStartDate(project.getStartDate());
        dto.setTargetDate(project.getTargetDate());
        dto.setWorkflowConfig(project.getWorkflowConfig());

        dto.setTeamMembers(project.getTeam().stream().map(m -> {
            ProjectMemberDTO mDto = new ProjectMemberDTO();
            mDto.setUserId(m.getUserId());
            mDto.setRole(m.getRole());
            mDto.setCapacityHours(m.getCapacityHours());
            mDto.setFullName(userService.getUserFullName(m.getUserId()));
            return mDto;
        }).collect(Collectors.toList()));

        return dto;
    }

    private ProjectSummaryDTO mapToSummaryDTO(Project project) {
        ProjectSummaryDTO dto = new ProjectSummaryDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setCode(project.getCode());
        dto.setClientName(project.getClientName());
        dto.setStatus(project.getStatus().name());
        dto.setType(project.getType());
        dto.setStartDate(project.getStartDate());
        dto.setTargetDate(project.getTargetDate());

        String tenantId = project.getTenantId();
        long totalTasks = taskRepository.countByProjectIdAndTenantId(project.getId(), tenantId);
        long completedTasks = taskRepository.countByProjectIdAndStatusAndTenantId(project.getId(), "DONE", tenantId);

        dto.setTaskCount((int) totalTasks);
        dto.setCompletedTaskCount((int) completedTasks);
        dto.setProgress(totalTasks > 0 ? (double) completedTasks / totalTasks : 0.0);
        dto.setActiveSprintsCount(
                (int) sprintRepository.countByProjectIdAndStatusAndTenantId(project.getId(), Sprint.SprintStatus.ACTIVE,
                        tenantId));
        dto.setTeamSize(project.getTeam().size());

        return dto;
    }

    // Keep for backward compatibility if needed, or refactor later
    @Transactional(readOnly = true)
    public List<ProjectDashboardResponse> getActiveProjectsStats() {
        return searchProjects(null, "ACTIVE", null).stream().map(s -> {
            ProjectDashboardResponse r = new ProjectDashboardResponse();
            r.setId(s.getId());
            r.setName(s.getName());
            r.setDescription(""); // Not in summary
            r.setStatus(s.getStatus());
            r.setTotalTasks(s.getTaskCount());
            r.setCompletedTasks(s.getCompletedTaskCount());
            r.setProgressPercentage((int) (s.getProgress() * 100));
            return r;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        return projectRepository.findAllByTenantId(TenantContext.getCurrentTenant());
    }

    @Transactional
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }
}

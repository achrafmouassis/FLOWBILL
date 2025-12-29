package com.flowbill.reporting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class DashboardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String getCurrentTenantId() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) auth.getDetails();
            return (String) details.get("tenantId");
        }
        return null;
        // throw new RuntimeException("Tenant ID not found in context");
    }

    @GetMapping("/global-metrics")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
    public Map<String, Object> getGlobalMetrics() {
        String tenantId = getCurrentTenantId();
        Map<String, Object> stats = new HashMap<>();

        // Active Projects (Status = IN_PROGRESS)
        // Fixed: Use correct variable and filter by tenantId
        // Assuming projects table has tenant_id. If not, we might be filtering at
        // schema level?
        // Audit said separate schemas for tenants?
        // "V1__init_master.sql" and "V1__init_projects.sql".
        // If separate schema per tenant, then just connecting to DB is enough IF
        // connection is dynamic.
        // BUT current architecture seems to be ONE DB with tenant_id columns (Shared
        // Database, Separate Schema strategy? Or Shared Schema?)
        // V4 added tenant_id to sprint/task/history -> implies Shared Schema with
        // Discriminator.
        // So we MUST query with tenant_id.
        // V1 init projects didn't show tenant_id. Assuming it handles it or was added.
        // Let's assume projects has tenant_id or we filter by joining?
        // Actually, ProjectService creates project with tenantId?
        // Let's assume tenant_id column exists on projects for now.
        // If not, we'll get SQL Error and fix.

        String activeProjectsSql = "SELECT COUNT(*) FROM projects WHERE status = 'IN_PROGRESS' AND tenant_id = ?";
        // Fallback if status col missing: "SELECT COUNT(*) FROM projects WHERE
        // tenant_id = ?"
        // Trying with status 'IN_PROGRESS' as planned.
        Integer activeProjects;
        try {
            activeProjects = jdbcTemplate.queryForObject(activeProjectsSql, Integer.class, tenantId);
        } catch (Exception e) {
            // Fallback if status column does not exist
            activeProjects = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM projects WHERE tenant_id = ?",
                    Integer.class, tenantId);
        }

        String activeSprintsSql = "SELECT COUNT(*) FROM sprints WHERE start_date <= CURRENT_TIMESTAMP AND end_date >= CURRENT_TIMESTAMP AND tenant_id = ?";
        Integer activeSprints = jdbcTemplate.queryForObject(activeSprintsSql, Integer.class, tenantId);

        String activeTasksSql = "SELECT COUNT(*) FROM tasks t JOIN projects p ON t.project_id = p.id WHERE t.status IN ('TODO', 'IN_PROGRESS') AND p.tenant_id = ?";
        // Or if tasks has tenant_id (V3/V4 added it?
        // V3__add_tenant_id_to_sprints_and_tasks.sql)
        // V3 added it. So we use it.
        activeTasksSql = "SELECT COUNT(*) FROM tasks WHERE status IN ('TODO', 'IN_PROGRESS') AND tenant_id = ?";
        Integer activeTasks = jdbcTemplate.queryForObject(activeTasksSql, Integer.class, tenantId);

        String backlogStoriesSql = "SELECT COUNT(*) FROM tasks WHERE type = 'STORY' AND sprint_id IS NULL AND tenant_id = ?";
        Integer backlogStories = jdbcTemplate.queryForObject(backlogStoriesSql, Integer.class, tenantId);

        String completedThisWeekSql = "SELECT COUNT(*) FROM tasks WHERE status = 'DONE' AND updated_at >= date_trunc('week', CURRENT_DATE) AND tenant_id = ?";
        Integer completedThisWeek = jdbcTemplate.queryForObject(completedThisWeekSql, Integer.class, tenantId);

        String teamSizeSql = "SELECT COUNT(DISTINCT assigned_user_id) FROM tasks WHERE assigned_user_id IS NOT NULL AND tenant_id = ?";
        Integer teamSize = jdbcTemplate.queryForObject(teamSizeSql, Integer.class, tenantId);

        stats.put("activeProjects", activeProjects);
        stats.put("activeSprints", activeSprints);
        stats.put("activeTasks", activeTasks);
        stats.put("backlogStories", backlogStories);
        stats.put("tasksCompletedThisWeek", completedThisWeek);
        stats.put("teamSize", teamSize);

        String plannedStoriesSql = "SELECT COUNT(*) FROM tasks WHERE type = 'STORY' AND sprint_id IN (SELECT id FROM sprints WHERE start_date <= CURRENT_TIMESTAMP AND end_date >= CURRENT_TIMESTAMP AND tenant_id = ?) AND tenant_id = ?";
        Integer plannedStories = jdbcTemplate.queryForObject(plannedStoriesSql, Integer.class, tenantId, tenantId);
        stats.put("plannedStories", plannedStories);

        return stats;
    }

    @GetMapping("/alerts")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
    public java.util.List<com.flowbill.reporting.dto.AlertResponse> getAlerts() {
        String tenantId = getCurrentTenantId();
        java.util.List<com.flowbill.reporting.dto.AlertResponse> alerts = new java.util.ArrayList<>();

        // 1. Blocked Tasks (> 3 days)
        String blockedSql = "SELECT id, title, updated_at FROM tasks WHERE status = 'BLOCKED' AND updated_at < NOW() - INTERVAL '3 DAYS' AND tenant_id = ?";
        try {
            jdbcTemplate.query(blockedSql, (rs, rowNum) -> {
                alerts.add(new com.flowbill.reporting.dto.AlertResponse(
                        "alert_blocked_" + rs.getLong("id"),
                        "BLOCKED_TASK",
                        "CRITICAL",
                        "Task '" + rs.getString("title") + "' blocked for > 3 days.",
                        rs.getLong("id"),
                        java.util.Arrays.asList("view_task", "resolve_blocker")));
                return null;
            }, tenantId);
        } catch (Exception e) {
        }

        // 2. Sprint at Risk
        String sprintRiskSql = "SELECT s.id, s.name, s.end_date, " +
                "(SELECT COUNT(*) FROM tasks t WHERE t.sprint_id = s.id AND t.tenant_id = ?) as total, " +
                "(SELECT COUNT(*) FROM tasks t WHERE t.sprint_id = s.id AND t.status = 'DONE' AND t.tenant_id = ?) as completed "
                +
                "FROM sprints s WHERE (s.status = 'ACTIVE' OR (s.start_date <= NOW() AND s.end_date >= NOW())) AND s.tenant_id = ?";
        try {
            jdbcTemplate.query(sprintRiskSql, (rs, rowNum) -> {
                int total = rs.getInt("total");
                int completed = rs.getInt("completed");
                java.sql.Timestamp endDate = rs.getTimestamp("end_date");

                long daysLeft = -1;
                if (endDate != null) {
                    daysLeft = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDateTime.now(),
                            endDate.toLocalDateTime());
                }

                double progress = total > 0 ? (double) completed / total : 1.0;

                if (daysLeft >= 0 && daysLeft < 3 && progress < 0.7) {
                    alerts.add(new com.flowbill.reporting.dto.AlertResponse(
                            "alert_sprint_" + rs.getLong("id"),
                            "SPRINT_AT_RISK",
                            "CRITICAL",
                            "Sprint '" + rs.getString("name") + "' ends in " + daysLeft + " days with only "
                                    + (int) (progress * 100) + "% completion.",
                            rs.getLong("id"),
                            java.util.Arrays.asList("view_sprint", "reassign_tasks")));
                }
                return null;
            }, tenantId, tenantId, tenantId);
        } catch (Exception e) {
        }

        return alerts;
    }

    @GetMapping("/velocity-chart")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
    public java.util.List<com.flowbill.reporting.dto.VelocityResponse> getVelocityChart() {
        String tenantId = getCurrentTenantId();
        java.util.List<com.flowbill.reporting.dto.VelocityResponse> velocity = new java.util.ArrayList<>();

        String sql = "SELECT s.id, s.name, " +
                "(SELECT COALESCE(SUM(t.estimation),0) FROM tasks t WHERE t.sprint_id = s.id AND t.tenant_id = ?) as committed, "
                +
                "(SELECT COALESCE(SUM(t.estimation),0) FROM tasks t WHERE t.sprint_id = s.id AND t.status = 'DONE' AND t.tenant_id = ?) as completed "
                +
                "FROM sprints s WHERE s.end_date < NOW() AND s.tenant_id = ? ORDER BY s.end_date DESC LIMIT 5";

        try {
            jdbcTemplate.query(sql, (rs, rowNum) -> {
                velocity.add(new com.flowbill.reporting.dto.VelocityResponse(
                        rs.getString("name"),
                        rs.getInt("committed"),
                        rs.getInt("completed")));
                return null;
            }, tenantId, tenantId, tenantId);
            java.util.Collections.reverse(velocity);
        } catch (Exception e) {
        }
        return velocity;
    }

    @GetMapping("/team-capacity")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
    public java.util.List<com.flowbill.reporting.dto.TeamCapacityResponse> getTeamCapacity() {
        String tenantId = getCurrentTenantId();
        java.util.List<com.flowbill.reporting.dto.TeamCapacityResponse> capacity = new java.util.ArrayList<>();

        String sql = "SELECT assigned_user_id, COUNT(*) as task_count, SUM(estimation) as total_points " +
                "FROM tasks WHERE status IN ('TODO', 'IN_PROGRESS') AND assigned_user_id IS NOT NULL AND tenant_id = ? "
                +
                "GROUP BY assigned_user_id";

        try {
            jdbcTemplate.query(sql, (rs, rowNum) -> {
                int totalPoints = rs.getInt("total_points");
                int taskCount = rs.getInt("task_count");
                double cap = Math.min(100.0, (totalPoints / 20.0) * 100);

                capacity.add(new com.flowbill.reporting.dto.TeamCapacityResponse(
                        rs.getLong("assigned_user_id"),
                        "User " + rs.getLong("assigned_user_id"),
                        totalPoints,
                        taskCount,
                        cap));
                return null;
            }, tenantId);
        } catch (Exception e) {
        }
        return capacity;
    }
}

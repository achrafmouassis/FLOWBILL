package com.flowbill.project.repository;

import com.flowbill.project.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
        java.util.Optional<Task> findByIdAndTenantId(Long id, String tenantId);

        java.util.List<Task> findByProjectId(Long projectId);

        java.util.List<Task> findBySprintId(Long sprintId);

        java.util.List<Task> findByProjectIdAndSprintIsNull(Long projectId);

        long countByProjectId(Long projectId);

        long countByProjectIdAndStatus(Long projectId, String status);

        // Agile / Backlog Queries

        @Query(value = "SELECT t FROM Task t " +
                        "WHERE t.project.id = :projectId " +
                        "AND t.type = 'STORY' " +
                        "AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) "
                        +
                        "AND (:moscow IS NULL OR t.moscowPriority = :moscow) " +
                        "AND (:sprintId IS NULL OR t.sprint.id = :sprintId) " +
                        "AND (:unplanned = true AND t.sprint IS NULL OR :unplanned = false) " +
                        "ORDER BY t.wsjfScore DESC NULLS LAST, t.manualOrder ASC", countQuery = "SELECT COUNT(t) FROM Task t "
                                        +
                                        "WHERE t.project.id = :projectId " +
                                        "AND t.type = 'STORY' " +
                                        "AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) "
                                        +
                                        "AND (:moscow IS NULL OR t.moscowPriority = :moscow) " +
                                        "AND (:sprintId IS NULL OR t.sprint.id = :sprintId) " +
                                        "AND (:unplanned = true AND t.sprint IS NULL OR :unplanned = false)")
        org.springframework.data.domain.Page<Task> searchBacklogStories(@Param("projectId") Long projectId,
                        @Param("search") String search,
                        @Param("moscow") String moscow,
                        @Param("sprintId") Long sprintId,
                        @Param("unplanned") Boolean unplanned,
                        org.springframework.data.domain.Pageable pageable);

        @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.type = 'STORY'")
        java.util.List<Task> findAllStoriesByProject(@Param("projectId") Long projectId);

        // --- New Agile Queries ---

        @Query("SELECT DISTINCT t FROM Task t " +
                        "WHERE t.project.id = :projectId " +
                        "AND t.type = 'STORY' " +
                        "AND (:moscow IS NULL OR t.moscowPriority IN :moscow) " +
                        "AND (:sprintId IS NULL OR t.sprint.id = :sprintId OR (:sprintId = -1 AND t.sprint IS NULL)) "
                        +
                        "AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) "
                        +
                        "ORDER BY CASE WHEN :sort = 'wsjf' THEN t.wsjfScore ELSE 0 END DESC, " +
                        "CASE WHEN :sort = 'moscow' THEN " +
                        "  CASE t.moscowPriority WHEN 'MUST_HAVE' THEN 1 WHEN 'SHOULD_HAVE' THEN 2 WHEN 'COULD_HAVE' THEN 3 WHEN 'WONT_HAVE' THEN 4 ELSE 5 END "
                        +
                        "ELSE 0 END ASC, " +
                        "CASE WHEN :sort = 'estimation' THEN t.estimation ELSE 0 END DESC, " +
                        "t.createdAt DESC")
        java.util.List<Task> searchBacklogStories(
                        @Param("projectId") Long projectId,
                        @Param("moscow") java.util.List<String> moscow,
                        @Param("sprintId") Long sprintId,
                        @Param("search") String search,
                        @Param("sort") String sort);

        @Query("SELECT DISTINCT t FROM Task t " +
                        "WHERE t.project.id = :projectId " +
                        "AND t.type = 'STORY' " +
                        "AND EXISTS (SELECT 1 FROM Task child WHERE child.parentStory = t AND child.assignedUserId = :userId) "
                        +
                        "AND (:moscow IS NULL OR t.moscowPriority IN :moscow) " +
                        "AND (:sprintId IS NULL OR t.sprint.id = :sprintId OR (:sprintId = -1 AND t.sprint IS NULL)) "
                        +
                        "AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) "
                        +
                        "ORDER BY CASE WHEN :sort = 'wsjf' THEN t.wsjfScore ELSE 0 END DESC, " +
                        "CASE WHEN :sort = 'moscow' THEN " +
                        "  CASE t.moscowPriority WHEN 'MUST_HAVE' THEN 1 WHEN 'SHOULD_HAVE' THEN 2 WHEN 'COULD_HAVE' THEN 3 WHEN 'WONT_HAVE' THEN 4 ELSE 5 END "
                        +
                        "ELSE 0 END ASC, " +
                        "t.createdAt DESC")
        java.util.List<Task> searchBacklogStoriesForDeveloper(
                        @Param("projectId") Long projectId,
                        @Param("userId") Long userId,
                        @Param("moscow") java.util.List<String> moscow,
                        @Param("sprintId") Long sprintId,
                        @Param("search") String search,
                        @Param("sort") String sort);

        @Query("SELECT DISTINCT t FROM Task t " +
                        "WHERE t.type = 'STORY' " +
                        "AND t.sprint IS NULL " +
                        "AND EXISTS (SELECT 1 FROM Task child WHERE child.parentStory = t AND child.assignedUserId = :userId)")
        java.util.List<Task> findStoriesWithAssignedTasksForDeveloper(@Param("userId") Long userId);

        @Query("SELECT t FROM Task t WHERE t.id IN (SELECT td.blocker.id FROM TaskDependency td WHERE td.blocked.id = :taskId)")
        java.util.List<Task> findBlockersForTask(@Param("taskId") Long taskId);

        @Query("SELECT COALESCE(SUM(t.estimation), 0) FROM Task t WHERE t.sprint.id = :sprintId AND t.type = 'STORY'")
        Integer sumEstimationBySprintId(@Param("sprintId") Long sprintId);

        @Query("SELECT COALESCE(SUM(t.estimation), 0) FROM Task t WHERE t.sprint.id = :sprintId AND t.status = 'DONE' AND t.type = 'STORY'")
        Integer sumCompletedStoryPointsBySprintId(@Param("sprintId") Long sprintId);

        boolean existsByProject_IdAndAssignedUserId(Long projectId, Long userId);

        // Stats Queries
        long countByProjectIdAndType(Long projectId, String type);

        long countByProjectIdAndTypeAndMoscowPriority(Long projectId, String type, String moscowPriority);

        @Query("SELECT CAST(COALESCE(SUM(t.estimation), 0) AS long) FROM Task t WHERE t.project.id = :projectId AND t.type = 'STORY'")
        Long sumEstimationByProjectId(@Param("projectId") Long projectId);

        @Query("SELECT COALESCE(AVG(t.wsjfScore), 0.0) FROM Task t WHERE t.project.id = :projectId AND t.type = 'STORY'")
        Double avgWsjfScoreByProjectId(@Param("projectId") Long projectId);

        @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.type = 'STORY' AND t.sprint IS NULL")
        Long countUnplannedStoriesByProjectId(@Param("projectId") Long projectId);
}

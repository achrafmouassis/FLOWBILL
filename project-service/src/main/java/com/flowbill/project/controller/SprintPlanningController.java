package com.flowbill.project.controller;

import com.flowbill.project.dto.SprintResponse;
import com.flowbill.project.entity.Sprint;
import com.flowbill.project.entity.Task;
import com.flowbill.project.repository.SprintRepository;
import com.flowbill.project.repository.TaskRepository;
import com.flowbill.project.service.ActivityLogService;
import com.flowbill.project.exception.NotFoundException;
import com.flowbill.project.exception.BadRequestException;
import com.flowbill.project.service.SprintCapacityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/sprints")
@RequiredArgsConstructor
public class SprintPlanningController {

        private final TaskRepository taskRepository;
        private final SprintRepository sprintRepository;
        private final SprintCapacityService capacityService;
        private final ActivityLogService activityLogService;

        @PutMapping("/{sprintId}/stories/{storyId}")
        @PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
        @Transactional
        public ResponseEntity<SprintResponse> addStoryToSprint(
                        @PathVariable Long sprintId,
                        @PathVariable Long storyId) {

                String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
                Sprint sprint = sprintRepository.findByIdAndTenantId(sprintId, tenantId)
                                .orElseThrow(() -> new NotFoundException("Sprint non trouvé"));

                if ("COMPLETED".equals(sprint.getStatus())) {
                        throw new BadRequestException("Impossible de modifier un sprint terminé");
                }

                Task story = taskRepository.findByIdAndTenantId(storyId, tenantId)
                                .orElseThrow(() -> new NotFoundException("Story non trouvée"));

                if (!"STORY".equals(story.getType())) {
                        throw new BadRequestException("Seules les stories peuvent être planifiées directement");
                }

                // Check dependencies
                List<Task> blockers = taskRepository.findBlockersForTask(storyId, tenantId);
                for (Task blocker : blockers) {
                        boolean blockerOk = (blocker.getSprint() != null
                                        && blocker.getSprint().getId().equals(sprintId))
                                        || "DONE".equals(blocker.getStatus());
                        if (!blockerOk) {
                                throw new BadRequestException(
                                                "Story bloquée par " + blocker.getId()
                                                                + " qui n'est ni terminée ni dans ce sprint");
                        }
                }

                // Check capacity
                if (capacityService.wouldExceedCapacity(sprint, story.getEstimation(), tenantId)) {
                        SprintCapacityService.SprintCapacityInfo capacityInfo = capacityService.getCapacityInfo(sprint,
                                        tenantId);
                        throw new BadRequestException(
                                        String.format("Ajout de cette story (%d heures) dépasserait la capacité du sprint. "
                                                        +
                                                        "Capacité: %d heures, Alloué: %d heures, Disponible: %d heures",
                                                        story.getEstimation(),
                                                        capacityInfo.getTotalCapacity(),
                                                        capacityInfo.getAllocatedHours(),
                                                        capacityInfo.getRemainingHours()));
                }

                story.setSprint(sprint);
                taskRepository.save(story);

                // Recalculate planned capacity
                // Note: targetVelocity is in table, let's use it if available or just count SP
                taskRepository.sumEstimationBySprintId(sprintId, tenantId);
                // If we had a column planned_story_points we would set it.
                // For now totalSP is derived from current tasks.

                activityLogService.logActivity(
                                "STORY_ADDED_TO_SPRINT",
                                "STORY",
                                storyId,
                                "Story ajoutée au sprint " + sprintId);

                return ResponseEntity.ok(SprintResponse.fromEntity(sprint));
        }

        @DeleteMapping("/{sprintId}/stories/{storyId}")
        @PreAuthorize("hasAuthority('ROLE_ADMIN_ENTREPRISE')")
        @Transactional
        public ResponseEntity<SprintResponse> removeStoryFromSprint(
                        @PathVariable Long sprintId,
                        @PathVariable Long storyId) {

                String tenantId = com.flowbill.project.config.TenantContext.getCurrentTenant();
                Sprint sprint = sprintRepository.findByIdAndTenantId(sprintId, tenantId)
                                .orElseThrow(() -> new NotFoundException("Sprint non trouvé"));

                Task story = taskRepository.findByIdAndTenantId(storyId, tenantId)
                                .orElseThrow(() -> new NotFoundException("Story non trouvée"));

                story.setSprint(null);
                taskRepository.save(story);

                activityLogService.logActivity(
                                "STORY_REMOVED_FROM_SPRINT",
                                "STORY",
                                storyId,
                                "Story retirée du sprint " + sprintId);

                return ResponseEntity.ok(SprintResponse.fromEntity(sprint));
        }
}

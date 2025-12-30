package com.flowbill.project.service;

import com.flowbill.project.dto.BurndownChartDTO;
import com.flowbill.project.entity.Sprint;
import com.flowbill.project.entity.SprintSnapshot;
import com.flowbill.project.repository.SprintRepository;
import com.flowbill.project.repository.SprintSnapshotRepository;
import com.flowbill.project.repository.TaskRepository;
import com.flowbill.project.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BurndownService {

    private final SprintRepository sprintRepository;
    private final SprintSnapshotRepository snapshotRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public void createInitialSnapshot(Long sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new NotFoundException("Sprint non trouvé"));

        Integer totalSP = taskRepository.sumEstimationBySprintId(sprintId);

        SprintSnapshot snapshot = new SprintSnapshot();
        snapshot.setSprint(sprint);
        snapshot.setSnapshotDate(sprint.getStartDate().toLocalDate());
        snapshot.setRemainingSp(totalSP);
        snapshot.setCompletedSp(0);

        // Initial snapshot for J0
        snapshotRepository.save(snapshot);
    }

    @Transactional
    public void createDailySnapshot(Long sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new NotFoundException("Sprint non trouvé"));

        if (!"ACTIVE".equals(sprint.getStatus())) {
            return;
        }

        LocalDate today = LocalDate.now();

        // Current Remaining SP (Total Planned - Completed in this sprint)
        Integer completedInSprint = taskRepository.sumCompletedStoryPointsBySprintId(sprintId);
        // We need total planned at start of sprint. If targetVelocity is used as
        // baseline:
        // Actually, let's use current total estimation in sprint for MVP.
        Integer totalPlanned = taskRepository.sumEstimationBySprintId(sprintId);
        Integer remainingSP = totalPlanned - completedInSprint;

        SprintSnapshot snapshot = new SprintSnapshot();
        snapshot.setSprint(sprint);
        snapshot.setSnapshotDate(today);
        snapshot.setRemainingSp(Math.max(0, remainingSP));
        snapshot.setCompletedSp(completedInSprint);

        snapshotRepository.save(snapshot);
    }

    public BurndownChartDTO getBurndownChart(Long sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new NotFoundException("Sprint non trouvé"));

        List<SprintSnapshot> snapshots = snapshotRepository.findBySprintIdOrderBySnapshotDateAsc(sprintId);

        List<String> dates = new ArrayList<>();
        List<Integer> actual = new ArrayList<>();
        List<Integer> ideal = new ArrayList<>();

        if (snapshots.isEmpty()) {
            return new BurndownChartDTO(dates, ideal, actual);
        }

        // Generate ideal curve
        LocalDate start = sprint.getStartDate().toLocalDate();
        LocalDate end = sprint.getEndDate().toLocalDate();
        long totalDays = ChronoUnit.DAYS.between(start, end);
        if (totalDays <= 0)
            totalDays = 1;

        Integer startSP = snapshots.get(0).getRemainingSp();

        for (SprintSnapshot snap : snapshots) {
            dates.add(snap.getSnapshotDate().toString());
            actual.add(snap.getRemainingSp());

            long daysPassed = ChronoUnit.DAYS.between(start, snap.getSnapshotDate());
            double idealVal = startSP - (startSP * (double) daysPassed / totalDays);
            ideal.add((int) Math.max(0, idealVal));
        }

        return new BurndownChartDTO(dates, ideal, actual);
    }
}

package com.flowbill.project.repository;

import com.flowbill.project.entity.SprintSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SprintSnapshotRepository extends JpaRepository<SprintSnapshot, Long> {
    List<SprintSnapshot> findBySprintIdOrderBySnapshotDateAsc(Long sprintId);
}

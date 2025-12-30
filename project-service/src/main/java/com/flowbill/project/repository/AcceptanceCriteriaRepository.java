package com.flowbill.project.repository;

import com.flowbill.project.entity.AcceptanceCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcceptanceCriteriaRepository extends JpaRepository<AcceptanceCriteria, Long> {
    java.util.List<AcceptanceCriteria> findByTaskId(Long taskId);

    void deleteByTaskId(Long taskId);
}

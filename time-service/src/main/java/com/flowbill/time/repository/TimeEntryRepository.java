package com.flowbill.time.repository;

import com.flowbill.time.entity.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    List<TimeEntry> findByApprovedTrue();
}

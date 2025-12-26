package com.flowbill.time.controller;

import com.flowbill.time.entity.TimeEntry;
import com.flowbill.time.repository.TimeEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/times")
public class TimeController {

    @Autowired
    private TimeEntryRepository timeEntryRepository;

    @GetMapping
    public List<TimeEntry> getEntries() {
        return timeEntryRepository.findAll();
    }

    @PostMapping
    public TimeEntry logTime(@RequestBody TimeEntry entry) {
        entry.setDate(LocalDateTime.now());
        entry.setApproved(false);
        // Validating taskId or userId would involve calling Project/Auth services here
        return timeEntryRepository.save(entry);
    }
    
    @PutMapping("/{id}/approve")
    public TimeEntry approveTime(@PathVariable Long id) {
        TimeEntry entry = timeEntryRepository.findById(id).orElseThrow();
        entry.setApproved(true);
        return timeEntryRepository.save(entry);
    }
    
    // Internal endpoint for billing
    @GetMapping("/billable")
    public List<TimeEntry> getBillableEntries() {
        return timeEntryRepository.findByApprovedTrue();
    }
}

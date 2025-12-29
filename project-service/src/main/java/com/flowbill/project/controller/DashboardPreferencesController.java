package com.flowbill.project.controller;

import com.flowbill.project.dto.UserDashboardPreferencesDto;
import com.flowbill.project.service.DashboardPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class DashboardPreferencesController {

    private final DashboardPreferencesService service;

    private Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof Long) {
            return (Long) auth.getDetails();
        }
        // Fallback or Exception?
        // For MVP if header missing, maybe returns 0 or throws
        // throw new RuntimeException("User ID not found in context");
        return 0L; // Or handle appropriately
    }

    @GetMapping
    public ResponseEntity<UserDashboardPreferencesDto> getPreferences() {
        Long userId = getCurrentUserId();
        if (userId == 0L)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(service.getPreferences(userId));
    }

    @PutMapping
    public ResponseEntity<Void> savePreferences(@RequestBody UserDashboardPreferencesDto dto) {
        Long userId = getCurrentUserId();
        if (userId == 0L)
            return ResponseEntity.badRequest().build();
        service.savePreferences(userId, dto);
        return ResponseEntity.ok().build();
    }
}

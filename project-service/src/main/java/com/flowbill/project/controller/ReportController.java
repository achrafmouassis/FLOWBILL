package com.flowbill.project.controller;

import com.flowbill.project.dto.*;
import com.flowbill.project.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/global-metrics")
    public ResponseEntity<GlobalMetricsDTO> getGlobalMetrics(@RequestParam Long projectId) {
        return ResponseEntity.ok(reportService.getGlobalMetrics(projectId));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<com.flowbill.project.dto.ReportAlertDTO>> getAlerts(@RequestParam Long projectId) {
        return ResponseEntity.ok(reportService.getAlerts(projectId));
    }

    @GetMapping("/team-load")
    public ResponseEntity<com.flowbill.project.dto.TeamLoadDTO> getTeamLoad(@RequestParam Long projectId) {
        return ResponseEntity.ok(reportService.getTeamLoad(projectId));
    }
}

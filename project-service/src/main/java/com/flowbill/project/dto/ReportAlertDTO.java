package com.flowbill.project.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportAlertDTO {
    private String id;
    private String type; // SPRINT_RISK, OVERLOAD, BLOCKED
    private String message;
    private Long entityId;
    private String severity; // CRITICAL, WARNING, INFO
}

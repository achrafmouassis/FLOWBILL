package com.flowbill.project.dto;

import lombok.Data;

@Data
public class UserDashboardPreferencesDto {
    private String layoutConfig;
    private String alertThresholds;
    private String displaySettings;
}

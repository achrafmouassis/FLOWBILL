package com.flowbill.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_dashboard_preferences")
@Data
public class UserDashboardPreferences {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "layout_config", columnDefinition = "TEXT")
    private String layoutConfig;

    @Column(name = "alert_thresholds", columnDefinition = "TEXT")
    private String alertThresholds;

    @Column(name = "display_settings", columnDefinition = "TEXT")
    private String displaySettings;
}

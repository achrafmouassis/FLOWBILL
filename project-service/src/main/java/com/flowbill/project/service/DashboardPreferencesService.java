package com.flowbill.project.service;

import com.flowbill.project.dto.UserDashboardPreferencesDto;
import com.flowbill.project.entity.UserDashboardPreferences;
import com.flowbill.project.repository.UserDashboardPreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardPreferencesService {

    private final UserDashboardPreferencesRepository repository;

    @Transactional(readOnly = true)
    public UserDashboardPreferencesDto getPreferences(Long userId) {
        return repository.findById(userId)
                .map(p -> {
                    UserDashboardPreferencesDto dto = new UserDashboardPreferencesDto();
                    dto.setLayoutConfig(p.getLayoutConfig());
                    dto.setAlertThresholds(p.getAlertThresholds());
                    dto.setDisplaySettings(p.getDisplaySettings());
                    return dto;
                })
                .orElse(new UserDashboardPreferencesDto()); // Return empty/default if not found
    }

    @Transactional
    public void savePreferences(Long userId, UserDashboardPreferencesDto dto) {
        UserDashboardPreferences prefs = repository.findById(userId)
                .orElse(new UserDashboardPreferences());

        if (prefs.getUserId() == null) {
            prefs.setUserId(userId);
        }

        prefs.setLayoutConfig(dto.getLayoutConfig());
        prefs.setAlertThresholds(dto.getAlertThresholds());
        prefs.setDisplaySettings(dto.getDisplaySettings());

        repository.save(prefs);
    }
}

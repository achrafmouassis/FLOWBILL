package com.flowbill.project.repository;

import com.flowbill.project.entity.UserDashboardPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDashboardPreferencesRepository extends JpaRepository<UserDashboardPreferences, Long> {
}

package com.flowbill.auth.repository;

import com.flowbill.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    java.util.List<User> findByTenantId(String tenantId);
}

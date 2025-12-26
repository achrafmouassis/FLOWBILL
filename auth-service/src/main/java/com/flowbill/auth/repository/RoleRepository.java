package com.flowbill.auth.repository;

import com.flowbill.auth.entity.Role;
import com.flowbill.auth.entity.Role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}

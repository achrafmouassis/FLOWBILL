package com.flowbill.auth.config;

import com.flowbill.auth.entity.Role;
import com.flowbill.auth.entity.User;
import com.flowbill.auth.repository.RoleRepository;
import com.flowbill.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Checking for initial data seeding...");

        // 1. Ensure Roles Exist
        Role superAdminRole = createRoleIfNotFound(Role.RoleName.ROLE_SUPER_ADMIN);
        createRoleIfNotFound(Role.RoleName.ROLE_ADMIN_ENTREPRISE);
        createRoleIfNotFound(Role.RoleName.ROLE_USER);

        // 2. Ensure Super Admin User Exists
        if (userRepository.findByEmail("admin@flowbill.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@flowbill.com");
            admin.setPassword(passwordEncoder.encode("password")); // Default password
            admin.setEnabled(true);
            admin.setRoles(Collections.singleton(superAdminRole));

            userRepository.save(admin);
            log.info("Seeded default Super Admin user: admin@flowbill.com / password");
        } else {
            log.info("Super Admin user already exists.");
        }

        // 3. Ensure Default Tenant Admin (Acme) Exists
        if (userRepository.findByEmail("admin@acme.com").isEmpty()) {
            User acmeAdmin = new User();
            acmeAdmin.setEmail("admin@acme.com");
            acmeAdmin.setPassword(passwordEncoder.encode("password"));
            acmeAdmin.setEnabled(true);
            acmeAdmin.setTenantId("acme");

            Role tenantAdminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN_ENTREPRISE)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            acmeAdmin.setRoles(Collections.singleton(tenantAdminRole));

            userRepository.save(acmeAdmin);
            log.info("Seeded default Tenant Admin: admin@acme.com / password");
        }
    }

    private Role createRoleIfNotFound(Role.RoleName name) {
        Optional<Role> roleOpt = roleRepository.findByName(name);
        if (roleOpt.isEmpty()) {
            Role role = new Role();
            role.setName(name);
            return roleRepository.save(role);
        }
        return roleOpt.get();
    }
}

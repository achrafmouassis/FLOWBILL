package com.flowbill.auth.service;

import com.flowbill.auth.entity.Role;
import com.flowbill.auth.entity.User;
import com.flowbill.auth.repository.RoleRepository;
import com.flowbill.auth.repository.UserRepository;
import com.flowbill.auth.util.JwtUtil;
import com.flowbill.auth.controller.AuthController.AuthResponse;
import com.flowbill.auth.controller.AuthController.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Extract primary role for token (simplify for MVP)
        String roleName = user.getRoles().stream()
                .findFirst()
                .map(role -> role.getName().name())
                .orElse("ROLE_USER");

        String token = jwtUtil.generateToken(user.getEmail(), roleName, user.getTenantId(), user.getId());
        return new AuthResponse(token);
    }

    public User register(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setTenantId(req.getTenantId());
        user.setFullName(req.getFullName());
        user.setTelephone(req.getTelephone());

        // Serialize competencies to JSON
        if (req.getCompetencies() != null) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                user.setCompetencies(mapper.writeValueAsString(req.getCompetencies()));
            } catch (Exception e) {
                // Log error but maybe don't block registration? Or block?
                // For now, let's treat it as empty or simple toString if failure, but exception
                // is better for integrity.
                throw new RuntimeException("Error converting competencies to JSON", e);
            }
        }

        Set<Role> roles = new HashSet<>();
        try {
            String roleStr = req.getRole().toUpperCase();
            // Map DEVELOPER to USER role
            if ("DEVELOPER".equals(roleStr)) {
                roleStr = "USER";
            }

            Role.RoleName name = Role.RoleName.valueOf("ROLE_" + roleStr);
            Role role = roleRepository.findByName(name)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            roles.add(role);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role provided: " + req.getRole());
        }

        user.setRoles(roles);

        return userRepository.save(user);
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public java.util.List<User> getUsersByTenant(String tenantId) {
        return userRepository.findByTenantId(tenantId);
    }

    public void updateCompetencies(Long userId, java.util.Map<String, java.util.List<String>> competencies) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            user.setCompetencies(mapper.writeValueAsString(competencies));
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error converting competencies to JSON", e);
        }
    }
}

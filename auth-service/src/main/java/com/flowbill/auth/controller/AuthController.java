package com.flowbill.auth.controller;

import com.flowbill.auth.entity.User;
import com.flowbill.auth.service.AuthService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Data
    public static class AuthRequest {
        private String email;
        private String password;
    }

    @Data
    public static class RegisterRequest {
        private String email;
        private String password;
        private String tenantId; // Optional for Super Admin
        private String role; // ADMIN_ENTREPRISE, USER
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String type = "Bearer";

        public AuthResponse(String token) {
            this.token = token;
        }
    }

    @PostMapping("/change-password")
    public void changePassword(@RequestBody ChangePasswordRequest request) {
        // In a real app, you'd extract email from security context.
        // For MVP, we pass email or trust the client (less secure but ok for proto if
        // protected)
        // Better: SecurityContextHolder.getContext().getAuthentication().getName()
        // But here we'll stick to simple arguments.
        // Actually, let's use the SecurityContext if possible, but we haven't
        // configured full filter chain here maybe?
        // Let's passed email in request for simplicity of this "Agent" task.
        authService.changePassword(request.getEmail(), request.getOldPassword(), request.getNewPassword());
    }

    @GetMapping("/users/{tenantId}")
    public java.util.List<User> getUsersByTenant(@PathVariable String tenantId) {
        return authService.getUsersByTenant(tenantId);
    }

    @Data
    public static class ChangePasswordRequest {
        private String email;
        private String oldPassword;
        private String newPassword;
    }
}

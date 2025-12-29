package com.flowbill.reporting.config;

import com.flowbill.common.multitenancy.TenantConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new HeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    public static class HeaderAuthenticationFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain filterChain)
                throws ServletException, IOException {

            String userSubject = request.getHeader(TenantConstants.USER_SUBJECT_HEADER);
            String rolesHeader = request.getHeader(TenantConstants.USER_ROLES_HEADER);
            String tenantIdHeader = request.getHeader(TenantConstants.TENANT_ID_HEADER);

            // Detailed Debugging
            if (userSubject != null || rolesHeader != null) {
                System.err.println("[DEBUG-AUTH] URI: " + request.getRequestURI());
                System.err.println("[DEBUG-AUTH] Subject: " + userSubject);
                System.err.println("[DEBUG-AUTH] Roles: " + rolesHeader);
                System.err.println("[DEBUG-AUTH] Tenant: " + tenantIdHeader);
            }

            if (userSubject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<SimpleGrantedAuthority> authorities = Collections.emptyList();

                if (rolesHeader != null && !rolesHeader.isEmpty()) {
                    // Robust cleaning of brackets if present
                    String cleanedRoles = rolesHeader.replace("[", "").replace("]", "").replace(" ", "");
                    authorities = Arrays.stream(cleanedRoles.split(","))
                            .filter(r -> !r.isEmpty())
                            .map(r -> new SimpleGrantedAuthority(r.trim()))
                            .collect(Collectors.toList());
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userSubject, null, authorities);

                java.util.Map<String, Object> details = new java.util.HashMap<>();
                details.put("tenantId", tenantIdHeader);

                String userIdHeader = request.getHeader(TenantConstants.USER_ID_HEADER);
                if (userIdHeader != null) {
                    try {
                        details.put("userId", Long.valueOf(userIdHeader));
                    } catch (Exception e) {
                        /* ignore */ }
                }

                authentication.setDetails(details);

                org.springframework.security.core.context.SecurityContext context = SecurityContextHolder
                        .createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);

                System.err.println(
                        "[DEBUG-AUTH] Established Context for " + userSubject + " with authorities "
                                + authorities.stream().map(Object::toString).collect(Collectors.joining(",")));
            } else if (userSubject == null) {
                System.err.println("[DEBUG-AUTH] No user subject found in headers");
            }

            filterChain.doFilter(request, response);
        }
    }
}

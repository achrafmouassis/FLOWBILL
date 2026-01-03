package com.flowbill.project.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Aspect
@Component
public class TenantFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Pointcut("execution(* org.springframework.data.repository.Repository+.*(..))")
    public void repositoryMethods() {
    }

    @Before("repositoryMethods()")
    public void enableTenantFilter() {
        String roles = TenantContext.getCurrentRoles();
        if (roles != null && roles.contains("ROLE_SUPER_ADMIN")) {
            // Bypass filter for SuperAdmin
            return;
        }

        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        }
    }
}

package com.flowbill.project.config;

import com.flowbill.common.multitenancy.TenantConstants;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String tenantId = req.getHeader(TenantConstants.TENANT_ID_HEADER);
        String userIdStr = req.getHeader(TenantConstants.USER_ID_HEADER);

        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
        }

        String roles = req.getHeader(TenantConstants.USER_ROLES_HEADER);
        if (roles != null) {
            TenantContext.setCurrentRoles(roles);
        }

        if (userIdStr != null) {
            try {
                TenantContext.setCurrentUserId(Long.parseLong(userIdStr));
            } catch (NumberFormatException e) {
                // Ignore or log
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}

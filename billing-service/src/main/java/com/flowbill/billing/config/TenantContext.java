package com.flowbill.billing.config;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class TenantContext {

    public String getCurrentTenant() {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        if (attribs instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) attribs).getRequest();
            String tenantId = request.getHeader("X-Tenant-Id");
            if (tenantId != null && !tenantId.isBlank()) {
                return tenantId;
            }
        }
        // Fallback or error
        throw new RuntimeException("No Tenant ID found in context");
    }
}

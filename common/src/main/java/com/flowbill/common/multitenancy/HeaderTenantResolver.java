package com.flowbill.common.multitenancy;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class HeaderTenantResolver implements TenantResolver {
    
    public static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    public String resolveTenantId(HttpServletRequest request) {
        return request.getHeader(TENANT_HEADER);
    }
}

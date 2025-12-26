package com.flowbill.common.multitenancy;

import jakarta.servlet.http.HttpServletRequest;

public interface TenantResolver {
    String resolveTenantId(HttpServletRequest request);
}

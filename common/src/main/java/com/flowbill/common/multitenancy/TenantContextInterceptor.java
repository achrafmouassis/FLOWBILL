package com.flowbill.common.multitenancy;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class TenantContextInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) {
            request.getHeaders().add(TenantConstants.TENANT_ID_HEADER, tenantId);
        }
        return execution.execute(request, body);
    }
}

package com.flowbill.gateway.filter;

import com.flowbill.common.multitenancy.TenantConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtAuthenticationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    @Value("${jwt.secret}")
    private String secret;

    public JwtAuthenticationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            try {
                Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

                // Extract info from claims
                String tenantId = claims.get("tenantId", String.class);
                Object userIdObj = claims.get("userId");
                String userId = (userIdObj != null) ? String.valueOf(userIdObj) : "";

                Object rolesObj = claims.get("roles");
                String rolesHeaderVal = "";
                if (rolesObj instanceof java.util.List) {
                    rolesHeaderVal = String.join(",", ((java.util.List<?>) rolesObj).stream()
                            .map(Object::toString).collect(java.util.stream.Collectors.toList()));
                } else if (rolesObj != null) {
                    rolesHeaderVal = rolesObj.toString().replace("[", "").replace("]", "").replace(" ", "");
                }
                final String rolesHeader = rolesHeaderVal;

                // Debug log Gateway
                // System.out.println("[GATEWAY-JWT] Sub: " + claims.getSubject() + ", Roles: "
                // + rolesHeader);

                // Mutate the request with all security headers
                org.springframework.http.server.reactive.ServerHttpRequest mutatedRequest = exchange.getRequest()
                        .mutate()
                        .headers(h -> {
                            h.set(TenantConstants.TENANT_ID_HEADER, tenantId != null ? tenantId : "DEFAULT");
                            h.set(TenantConstants.USER_SUBJECT_HEADER, claims.getSubject());
                            h.set(TenantConstants.USER_ID_HEADER, userId != null && !userId.isEmpty() ? userId : "0");
                            h.set(TenantConstants.USER_ROLES_HEADER, rolesHeader);
                        })
                        .build();

                // Create a new exchange with the mutated request
                org.springframework.web.server.ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(mutatedRequest)
                        .build();

                return chain.filter(mutatedExchange);

            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {
        // Put configuration properties here
    }
}

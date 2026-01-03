package com.flowbill.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

/**
 * Service for fetching user information from auth-service.
 * Uses caching to minimize inter-service calls.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    @Value("${SERVICE_AUTH_URL:http://localhost:8081}")
    private String authServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetches the full name of a user by ID.
     * Results are cached to improve performance.
     * 
     * @param userId the user ID
     * @return the user's full name, or "User [ID]" if not found
     */
    @Cacheable(value = "userNames", key = "#userId", unless = "#result == null")
    public String getUserFullName(Long userId) {
        if (userId == null || userId == 0) {
            return "System";
        }

        try {
            String url = authServiceUrl + "/auth/users/" + userId + "/name";
            String fullName = restTemplate.getForObject(url, String.class);

            if (fullName != null && !fullName.isEmpty()) {
                return fullName;
            }
        } catch (RestClientException e) {
            log.warn("Failed to fetch user name for userId {}: {}", userId, e.getMessage());
        }

        // Fallback to placeholder if auth-service is unavailable
        return "User " + userId;
    }

    /**
     * Fetches multiple user names at once.
     * More efficient than individual calls for bulk operations.
     * 
     * @param userIds list of user IDs
     * @return map of userId to full name
     */
    @Cacheable(value = "userNamesBulk", key = "#userIds.hashCode()")
    public java.util.Map<Long, String> getUserFullNames(java.util.List<Long> userIds) {
        java.util.Map<Long, String> result = new java.util.HashMap<>();

        for (Long userId : userIds) {
            result.put(userId, getUserFullName(userId));
        }

        return result;
    }
}

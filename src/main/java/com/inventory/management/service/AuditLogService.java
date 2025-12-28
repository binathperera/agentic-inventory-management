package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.model.AuditLog;
import com.inventory.management.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Log an action with all details
     */
    public AuditLog logAction(String entityType, String entityId, String actionType,
            String description, Map<String, Object> oldValues,
            Map<String, Object> newValues) {
        return logAction(entityType, entityId, actionType, description, oldValues, newValues,
                AuditLog.Status.SUCCESS.value, null);
    }

    /**
     * Log an action with status and error message
     */
    public AuditLog logAction(String entityType, String entityId, String actionType,
            String description, Map<String, Object> oldValues,
            Map<String, Object> newValues, String status, String errorMessage) {

        String tenantId = TenantContext.getTenantId();
        String userId = getCurrentUserId();
        String username = getCurrentUsername();

        AuditLog auditLog = AuditLog.builder()
                .logId(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .userId(userId)
                .username(username)
                .entityType(entityType)
                .entityId(entityId)
                .actionType(actionType)
                .description(description)
                .oldValues(oldValues)
                .newValues(newValues)
                .status(status)
                .errorMessage(errorMessage)
                .ipAddress(getClientIpAddress())
                .userAgent(getUserAgent())
                .requestPath(getRequestPath())
                .requestMethod(getRequestMethod())
                .timestamp(Instant.now())
                .build();

        return auditLogRepository.save(auditLog);
    }

    /**
     * Log a failed action
     */
    public AuditLog logFailedAction(String entityType, String entityId, String actionType,
            String description, String errorMessage) {
        return logAction(entityType, entityId, actionType, description, null, null,
                AuditLog.Status.FAILURE.value, errorMessage);
    }

    /**
     * Log authentication event
     */
    public AuditLog logAuthenticationEvent(String userId, String username, boolean success, String errorMessage) {
        String tenantId = TenantContext.getTenantId();

        AuditLog auditLog = AuditLog.builder()
                .logId(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .userId(userId)
                .username(username)
                .entityType("Authentication")
                .actionType(AuditLog.ActionType.LOGIN.value)
                .description(success ? "User login successful" : "User login failed")
                .status(success ? AuditLog.Status.SUCCESS.value : AuditLog.Status.FAILURE.value)
                .errorMessage(errorMessage)
                .ipAddress(getClientIpAddress())
                .userAgent(getUserAgent())
                .requestPath(getRequestPath())
                .requestMethod(getRequestMethod())
                .timestamp(Instant.now())
                .build();

        return auditLogRepository.save(auditLog);
    }

    /**
     * Get audit logs for current tenant with pagination
     */
    public Page<AuditLog> getTenantAuditLogs(Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByTenantId(tenantId, pageable);
    }

    /**
     * Get audit logs for a specific user
     */
    public Page<AuditLog> getUserAuditLogs(String userId, Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByTenantIdAndUserId(tenantId, userId, pageable);
    }

    /**
     * Get audit logs for a specific entity
     */
    public Page<AuditLog> getEntityAuditLogs(String entityType, String entityId, Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByTenantIdAndEntityTypeAndEntityId(tenantId, entityType, entityId, pageable);
    }

    /**
     * Get audit logs by action type
     */
    public Page<AuditLog> getActionTypeAuditLogs(String actionType, Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByTenantIdAndActionType(tenantId, actionType, pageable);
    }

    /**
     * Get audit logs within a time range
     */
    public Page<AuditLog> getAuditLogsByDateRange(Instant startTime, Instant endTime, Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByTenantIdAndTimestampBetween(tenantId, startTime, endTime, pageable);
    }

    /**
     * Get audit logs for a user within a time range
     */
    public Page<AuditLog> getUserAuditLogsByDateRange(String userId, Instant startTime, Instant endTime,
            Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByTenantIdAndUserIdAndTimestampBetween(tenantId, userId, startTime, endTime,
                pageable);
    }

    /**
     * Get failed operations
     */
    public Page<AuditLog> getFailedOperations(Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByTenantIdAndStatus(tenantId, AuditLog.Status.FAILURE.value, pageable);
    }

    /**
     * Get recent user activity
     */
    public Page<AuditLog> getEntityHistory(String entityType, String entityId, Instant startTime, Instant endTime,
            Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByTenantIdAndEntityTypeAndEntityIdAndTimestampBetween(
                tenantId, entityType, entityId, startTime, endTime, pageable);
    }

    /**
     * Get audit report for a user
     */
    public Page<AuditLog> getAuditReport(String userId, String actionType, Instant startTime, Instant endTime,
            Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByTenantUserActionAndDateRange(tenantId, userId, actionType, startTime, endTime,
                pageable);
    }

    // Helper methods

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // In a real scenario, you'd extract the user ID from UserDetails
            // For now, we'll use the username as identifier
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return "ANONYMOUS";
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return "ANONYMOUS";
    }

    private String getClientIpAddress() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // If request context is not available, return null
        }
        return null;
    }

    private String getUserAgent() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (requestAttributes != null) {
                return requestAttributes.getRequest().getHeader("User-Agent");
            }
        } catch (Exception e) {
            // If request context is not available, return null
        }
        return null;
    }

    private String getRequestPath() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (requestAttributes != null) {
                return requestAttributes.getRequest().getRequestURI();
            }
        } catch (Exception e) {
            // If request context is not available, return null
        }
        return null;
    }

    private String getRequestMethod() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (requestAttributes != null) {
                return requestAttributes.getRequest().getMethod();
            }
        } catch (Exception e) {
            // If request context is not available, return null
        }
        return null;
    }
}

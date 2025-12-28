package com.inventory.management.aspect;

import com.inventory.management.model.AuditLog;
import com.inventory.management.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogService auditLogService;

    /**
     * Intercept all POST requests (CREATE operations)
     */
    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object auditCreateOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        return auditOperation(joinPoint, AuditLog.ActionType.CREATE.value, null);
    }

    /**
     * Intercept all PUT requests (UPDATE operations)
     */
    @Around("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public Object auditUpdateOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        return auditOperation(joinPoint, AuditLog.ActionType.UPDATE.value, null);
    }

    /**
     * Intercept all DELETE requests (DELETE operations)
     */
    @Around("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object auditDeleteOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        return auditOperation(joinPoint, AuditLog.ActionType.DELETE.value, null);
    }

    /**
     * Intercept all GET requests (READ operations)
     */
    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object auditReadOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // For sensitive reads, log them. For high-volume reads, consider filtering
        // return auditOperation(joinPoint, AuditLog.ActionType.READ.value, null);

        // For now, skip logging READ operations to reduce log volume
        return joinPoint.proceed();
    }

    /**
     * Core audit logic
     */
    private Object auditOperation(ProceedingJoinPoint joinPoint, String actionType, Map<String, Object> oldValues)
            throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        Object[] args = joinPoint.getArgs();
        String entityType = extractEntityType(className);
        String entityId = extractEntityId(args, actionType);

        Object result = null;
        Exception exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            String status = exception == null ? AuditLog.Status.SUCCESS.value : AuditLog.Status.FAILURE.value;
            String errorMessage = exception != null ? exception.getMessage() : null;

            Map<String, Object> newValues = extractNewValues(result, actionType);
            String description = String.format("%s operation on %s", actionType, entityType);

            try {
                AuditLog auditLog = auditLogService.logAction(
                        entityType,
                        entityId,
                        actionType,
                        description,
                        oldValues,
                        newValues,
                        status,
                        errorMessage);

                auditLog.setDurationMs(duration);
                log.info("Audit logged: {} - {} - {}", entityType, actionType, status);
            } catch (Exception e) {
                log.error("Failed to log audit", e);
            }
        }
    }

    /**
     * Extract entity type from class name (e.g., ProductController -> Product)
     */
    private String extractEntityType(String className) {
        return className.replace("Controller", "").replace("Service", "");
    }

    /**
     * Extract entity ID from method arguments
     */
    private String extractEntityId(Object[] args, String actionType) {
        if (args == null || args.length == 0) {
            return null;
        }

        // For DELETE/UPDATE operations, the first arg is usually the ID
        if ((AuditLog.ActionType.DELETE.value.equals(actionType) ||
                AuditLog.ActionType.UPDATE.value.equals(actionType)) &&
                args[0] instanceof String) {
            return (String) args[0];
        }

        // For CREATE operations, the entity might be in the first argument
        if (AuditLog.ActionType.CREATE.value.equals(actionType) && args.length > 0) {
            Object firstArg = args[0];
            if (firstArg instanceof String) {
                return (String) firstArg;
            }
            // You can add logic to extract ID from entity objects
        }

        return null;
    }

    /**
     * Extract new values from the result
     */
    private Map<String, Object> extractNewValues(Object result, String actionType) {
        Map<String, Object> newValues = new HashMap<>();

        if (result != null && !AuditLog.ActionType.DELETE.value.equals(actionType)) {
            // For CREATE/UPDATE operations, capture the returned entity
            newValues.put("result_type", result.getClass().getSimpleName());
            newValues.put("result", result.toString());
        }

        return newValues.isEmpty() ? null : newValues;
    }
}

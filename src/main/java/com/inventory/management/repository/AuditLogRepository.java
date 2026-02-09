package com.inventory.management.repository;

import com.inventory.management.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

    /**
     * Find all audit logs for a tenant with pagination
     */
    Page<AuditLog> findByTenantId(String tenantId, Pageable pageable);

    /**
     * Find all audit logs for a specific user within a tenant
     */
    Page<AuditLog> findByTenantIdAndUserId(String tenantId, String userId, Pageable pageable);

    /**
     * Find audit logs for a specific entity
     */
    Page<AuditLog> findByTenantIdAndEntityTypeAndEntityId(String tenantId, String entityType, String entityId,
            Pageable pageable);

    /**
     * Find audit logs by action type
     */
    Page<AuditLog> findByTenantIdAndActionType(String tenantId, String actionType, Pageable pageable);

    /**
     * Find audit logs within a time range for a tenant
     */
    Page<AuditLog> findByTenantIdAndTimestampBetween(String tenantId, Instant startTime, Instant endTime,
            Pageable pageable);

    /**
     * Find all logs for a user within a time range
     */
    Page<AuditLog> findByTenantIdAndUserIdAndTimestampBetween(String tenantId, String userId, Instant startTime,
            Instant endTime, Pageable pageable);

    /**
     * Find logs by entity and time range
     */
    Page<AuditLog> findByTenantIdAndEntityTypeAndEntityIdAndTimestampBetween(String tenantId, String entityType,
            String entityId, Instant startTime, Instant endTime, Pageable pageable);

    /**
     * Find logs by action type and status
     */
    Page<AuditLog> findByTenantIdAndActionTypeAndStatus(String tenantId, String actionType, String status,
            Pageable pageable);

    /**
     * Custom query to find logs with complex filters
     */
    @Query("{ 'tenant_id': ?0, 'user_id': ?1, 'action_type': ?2, 'timestamp': { $gte: ?3, $lte: ?4 } }")
    Page<AuditLog> findByTenantUserActionAndDateRange(String tenantId, String userId, String actionType,
            Instant startTime, Instant endTime, Pageable pageable);

    /**
     * Find all failed operations
     */
    Page<AuditLog> findByTenantIdAndStatus(String tenantId, String status, Pageable pageable);

    /**
     * Get recent logs for a user
     */
    List<AuditLog> findTop10ByTenantIdAndUserIdOrderByTimestampDesc(String tenantId, String userId);
}

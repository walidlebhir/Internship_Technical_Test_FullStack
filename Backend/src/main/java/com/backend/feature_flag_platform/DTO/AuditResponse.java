package com.backend.feature_flag_platform.DTO;

import com.backend.feature_flag_platform.Entity.Enum.AuditAction;
import com.backend.feature_flag_platform.Entity.Enum.EntityType;

import java.time.LocalDateTime;

/**
 * DTO returned by the audit trail endpoint.
 * <p>
 * Contains a summary of an audited operation:
 * who performed it, on which entity, what action, and when.
 */
public record AuditResponse(
        Long id,
        LocalDateTime timestamp,
        AuditAction action,
        EntityType entityType,
        String entityId,
        String who
) {
}

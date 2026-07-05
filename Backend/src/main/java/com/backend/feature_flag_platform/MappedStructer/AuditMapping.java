package com.backend.feature_flag_platform.MappedStructer;

import com.backend.feature_flag_platform.DTO.AuditResponse;
import com.backend.feature_flag_platform.Entity.AuditEntry;
import org.springframework.stereotype.Component;

/**
 * Maps an {@link AuditEntry} entity to an {@link AuditResponse} DTO.

 */
@Component
public class AuditMapping {

    /**
     * Converts a persisted {@link AuditEntry} into a lightweight response DTO.
     */
    public AuditResponse mapAuditToResponse(AuditEntry audit) {
        return new AuditResponse(
                audit.getId(),
                audit.getTimestamp(),
                audit.getAction(),
                audit.getEntityType(),
                audit.getEntityId(),
                audit.getWho()
        );
    }
}

package com.backend.feature_flag_platform.Service;

import com.backend.feature_flag_platform.DTO.AuditResponse;
import com.backend.feature_flag_platform.Entity.AuditEntry;
import com.backend.feature_flag_platform.Entity.Enum.AuditAction;
import com.backend.feature_flag_platform.Entity.Enum.EntityType;
import com.backend.feature_flag_platform.MappedStructer.AuditMapping;
import com.backend.feature_flag_platform.Repository.AuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for recording and querying audit trail entries.
 * <p>
 * This service is used by the {@code AuditAspect} to persist audit entries
 * automatically when annotated methods are invoked. It also exposes
 * paginated queries for the audit endpoint.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditRepository auditRepository;
    private final AuditMapping auditMapping;

    public AuditService(AuditRepository auditRepository, AuditMapping auditMapping) {
        this.auditRepository = auditRepository;
        this.auditMapping = auditMapping;
    }

    /**
     * Persists a new audit entry.
     * <p>
     * This method is called by the {@code AuditAspect} after a successful
     * CREATE, UPDATE, or DELETE operation. The timestamp is set automatically
     * by the {@code @PrePersist} callback on {@link AuditEntry}.
     *
     * @param action     the operation performed (CREATE, UPDATE, DELETE)
     * @param entityType the type of entity affected
     * @param entityId   the string representation of the entity's primary key
     * @param who        the user who performed the operation
     */
    @Transactional
    public void record(AuditAction action, EntityType entityType, String entityId, String who) {
        AuditEntry entry = AuditEntry.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .who(who)
                .build();
        auditRepository.save(entry);
        log.debug("Audit entry saved: {} {} #{}", action, entityType, entityId);
    }

    /**
     * Retrieves a paginated list of all audit entries, ordered by timestamp
     * descending (most recent first).
     *
     * @param pageable pagination parameters
     * @return a page of {@link AuditResponse} DTOs
     */
    @Transactional(readOnly = true)
    public Page<AuditResponse> findAll(Pageable pageable) {
        return auditRepository.findAllByOrderByTimestampDesc(pageable)
                .map(auditMapping::mapAuditToResponse);
    }

    /**
     * Retrieves a paginated list of audit entries filtered by entity type,
     * ordered by timestamp descending.
     *
     * @param entityType the entity type to filter by (may be null for all)
     * @param pageable   pagination parameters
     * @return a page of {@link AuditResponse} DTOs
     */
    @Transactional(readOnly = true)
    public Page<AuditResponse> findByEntityType(EntityType entityType, Pageable pageable) {
        if (entityType == null) {
            return findAll(pageable);
        }
        return auditRepository.findByEntityTypeOrderByTimestampDesc(entityType, pageable)
                .map(auditMapping::mapAuditToResponse);
    }
}

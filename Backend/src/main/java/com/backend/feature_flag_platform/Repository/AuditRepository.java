package com.backend.feature_flag_platform.Repository;

import com.backend.feature_flag_platform.Entity.AuditEntry;
import com.backend.feature_flag_platform.Entity.Enum.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for persisting and querying {@link AuditEntry} records.
 * <p>
 * Provides paginated queries ordered by timestamp descending (most recent first)
 * with optional filtering by {@link EntityType}.
 */
@Repository
public interface AuditRepository extends JpaRepository<AuditEntry, Long> {

    /**
     * Retrieves all audit entries ordered by timestamp, most recent first.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a page of audit entries
     */
    Page<AuditEntry> findAllByOrderByTimestampDesc(Pageable pageable);

    /**
     * Retrieves audit entries for a specific entity type, ordered by timestamp descending.
     *
     * @param entityType the type of entity to filter by (DOMAIN, FEATURE, STRATEGY)
     * @param pageable   pagination parameters
     * @return a filtered page of audit entries
     */
    Page<AuditEntry> findByEntityTypeOrderByTimestampDesc(EntityType entityType, Pageable pageable);
}

package com.backend.feature_flag_platform.Repository;

import com.backend.feature_flag_platform.Entity.AuditEntry;
import com.backend.feature_flag_platform.Entity.Enum.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for persisting and querying {@link AuditEntry} records.
 */
@Repository
public interface AuditRepository extends JpaRepository<AuditEntry, Long> {


    Page<AuditEntry> findAllByOrderByTimestampDesc(Pageable pageable);

    Page<AuditEntry> findByEntityTypeOrderByTimestampDesc(EntityType entityType, Pageable pageable);
}

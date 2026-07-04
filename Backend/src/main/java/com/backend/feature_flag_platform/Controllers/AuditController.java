package com.backend.feature_flag_platform.Controllers;

import com.backend.feature_flag_platform.DTO.AuditResponse;
import com.backend.feature_flag_platform.Entity.Enum.EntityType;
import com.backend.feature_flag_platform.Service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for the audit trail.
 * <p>
 * Exposes a paginated, filterable endpoint to browse historical
 * audit entries across all entity types.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Retrieves audit trail entries with pagination and optional type filtering.
     * <p>
     * Results are always sorted by timestamp descending (most recent first).
     *
     * @param entityType optional filter by entity type (DOMAIN, FEATURE, STRATEGY)
     * @param pageable   pagination parameters (default: page 0, size 20, sort by timestamp desc)
     * @return a paginated list of {@link AuditResponse}
     */
    @GetMapping
    public ResponseEntity<Page<AuditResponse>> getAuditEntries(
            @RequestParam(required = false) EntityType entityType,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AuditResponse> result = auditService.findByEntityType(entityType, pageable);
        return ResponseEntity.ok(result);
    }
}

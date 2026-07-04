package com.backend.feature_flag_platform.Controllers;

import com.backend.feature_flag_platform.DTO.DomainRequest;
import com.backend.feature_flag_platform.DTO.DomainResponse;
import com.backend.feature_flag_platform.Service.DomainService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/domains")
public class DomainController {

    private final DomainService domainService;

    public DomainController(DomainService domainService) {
        this.domainService = domainService;
    }

    /**
     * Creates a new domain.
     */
    @PostMapping
    public ResponseEntity<DomainResponse> createDomain(@Valid @RequestBody DomainRequest request) {
        DomainResponse response = domainService.createDomain(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a domain by its UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DomainResponse> getDomainById(@PathVariable UUID id) {
        DomainResponse response = domainService.getDomainById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all domains.
     */
    @GetMapping
    public ResponseEntity<List<DomainResponse>> getAllDomains() {
        List<DomainResponse> response = domainService.getAllDomains();
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing domain.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DomainResponse> updateDomain(@PathVariable UUID id, @Valid @RequestBody DomainRequest request) {
        DomainResponse response = domainService.updateDomain(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a domain by its UUID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDomain(@PathVariable UUID id) {
        domainService.deleteDomain(id);
        return ResponseEntity.noContent().build();
    }
}

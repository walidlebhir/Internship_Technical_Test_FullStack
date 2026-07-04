package com.backend.feature_flag_platform.Service;

import com.backend.feature_flag_platform.DTO.DomainRequest;
import com.backend.feature_flag_platform.DTO.DomainResponse;
import com.backend.feature_flag_platform.Entity.Domain;
import com.backend.feature_flag_platform.MappedStructer.DomainMapping;
import com.backend.feature_flag_platform.Repository.DomainRepository;
import com.backend.feature_flag_platform.exception.DuplicateResourceException;
import com.backend.feature_flag_platform.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DomainService {

    private final DomainRepository domainRepository;
    private final DomainMapping domainMapping;

    public DomainService(DomainRepository domainRepository, DomainMapping domainMapping) {
        this.domainRepository = domainRepository;
        this.domainMapping = domainMapping;
    }

    /**
     * Creates a new domain after verifying no duplicate name exists.
     */
    public DomainResponse createDomain(DomainRequest request) {
        Domain existing = domainRepository.findByName(request.name());
        if (existing != null) {
            throw new DuplicateResourceException("Domain with name '" + request.name() + "' already exists");
        }
        Domain domain = Domain.builder()
                .name(request.name())
                .description(request.description())
                .build();
        domainRepository.save(domain);
        return domainMapping.mapDomainToResponse(domain);
    }

    /**
     * Updates an existing domain identified by its UUID.
     */
    public DomainResponse updateDomain(UUID id, DomainRequest request) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found with id: " + id));
        domain.setName(request.name());
        domain.setDescription(request.description());
        Domain saved = domainRepository.save(domain);
        return domainMapping.mapDomainToResponse(saved);
    }

    /**
     * Deletes a domain by its UUID. Throws an exception if the domain does not exist.
     */
    public void deleteDomain(UUID id) {
        if (!domainRepository.existsById(id)) {
            throw new ResourceNotFoundException("Domain not found with id: " + id);
        }
        domainRepository.deleteById(id);
    }

    /**
     * Retrieves a single domain by its UUID.
     */
    public DomainResponse getDomainById(UUID id) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found with id: " + id));
        return domainMapping.mapDomainToResponse(domain);
    }

    /**
     * Retrieves all domains.
     */
    public List<DomainResponse> getAllDomains() {
        return domainRepository.findAll().stream()
                .map(domainMapping::mapDomainToResponse)
                .toList();
    }
}

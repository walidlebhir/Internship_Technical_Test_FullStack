package com.backend.feature_flag_platform.Service;

import com.backend.feature_flag_platform.DTO.FeatureRequest;
import com.backend.feature_flag_platform.DTO.FeatureResponse;
import com.backend.feature_flag_platform.Entity.Domain;
import com.backend.feature_flag_platform.Entity.Feature;
import com.backend.feature_flag_platform.MappedStructer.FeatureMapping;
import com.backend.feature_flag_platform.Repository.DomainRepository;
import com.backend.feature_flag_platform.Repository.FeatureRepository;
import com.backend.feature_flag_platform.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FeatureService {
    private final FeatureRepository featureRepository;
    private final DomainRepository domainRepository;
    private final FeatureMapping featureMapping;
    private final FeatureEvaluationService featureEvaluationService;

    public FeatureService(
            FeatureRepository featureRepository,
            DomainRepository domainRepository,
            FeatureMapping featureMapping,
            FeatureEvaluationService featureEvaluationService
    ) {
        this.featureRepository = featureRepository;
        this.domainRepository = domainRepository;
        this.featureMapping = featureMapping;
        this.featureEvaluationService = featureEvaluationService;
    }

    public FeatureResponse createFeature(FeatureRequest request) {
        Domain domain = domainRepository.findById(request.id_domain())
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found with id: " + request.id_domain()));
        Feature feature = Feature.builder()
                .key(request.key())
                .description(request.description())
                .enabled(false)
                .domain(domain)
                .build();
        featureRepository.save(feature);
        return featureMapping.mapFeatureToResponse(feature);
    }

    public FeatureResponse getFeatureById(Long id) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id));
        return featureMapping.mapFeatureToResponse(feature);
    }

    public List<FeatureResponse> getAllFeatures() {
        return featureRepository.findAll().stream()
                .map(featureMapping::mapFeatureToResponse)
                .toList();
    }

    public FeatureResponse updateFeature(Long id, FeatureRequest request) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id));
        feature.setKey(request.key());
        feature.setDescription(request.description());
        if (request.id_domain() != null) {
            Domain domain = domainRepository.findById(request.id_domain())
                    .orElseThrow(() -> new ResourceNotFoundException("Domain not found with id: " + request.id_domain()));
            feature.setDomain(domain);
        }
        Feature saved = featureRepository.save(feature);
        featureEvaluationService.evictAllEvaluationCaches();
        return featureMapping.mapFeatureToResponse(saved);
    }

    public void deleteFeature(Long id) {
        if (!featureRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feature not found with id: " + id);
        }
        featureRepository.deleteById(id);
        featureEvaluationService.evictAllEvaluationCaches();
    }

    public FeatureResponse enableFeature(Long id) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id));
        feature.setEnabled(true);
        Feature saved = featureRepository.save(feature);
        featureEvaluationService.evictAllEvaluationCaches();
        return featureMapping.mapFeatureToResponse(saved);
    }

    public FeatureResponse disableFeature(Long id) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id));
        feature.setEnabled(false);
        Feature saved = featureRepository.save(feature);
        featureEvaluationService.evictAllEvaluationCaches();
        return featureMapping.mapFeatureToResponse(saved);
    }
}

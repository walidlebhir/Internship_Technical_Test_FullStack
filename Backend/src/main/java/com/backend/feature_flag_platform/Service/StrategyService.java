package com.backend.feature_flag_platform.Service;

import com.backend.feature_flag_platform.DTO.StrategyRequest;
import com.backend.feature_flag_platform.DTO.StrategyResponse;
import com.backend.feature_flag_platform.Entity.Enum.StrategyType;
import com.backend.feature_flag_platform.Entity.Feature;
import com.backend.feature_flag_platform.Entity.Strategy;
import com.backend.feature_flag_platform.MappedStructer.StrategyMapping;
import com.backend.feature_flag_platform.Repository.FeatureRepository;
import com.backend.feature_flag_platform.Repository.StrategyRepository;
import com.backend.feature_flag_platform.exception.InvalidStrategyConfigException;
import com.backend.feature_flag_platform.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StrategyService {

    private final StrategyRepository strategyRepository;
    private final FeatureRepository featureRepository;
    private final StrategyMapping strategyMapping;
    private final ObjectMapper objectMapper;
    private final FeatureEvaluationService featureEvaluationService;

    public StrategyService(
            StrategyRepository strategyRepository,
            FeatureRepository featureRepository,
            StrategyMapping strategyMapping,
            ObjectMapper objectMapper,
            FeatureEvaluationService featureEvaluationService
    ) {
        this.strategyRepository = strategyRepository;
        this.featureRepository = featureRepository;
        this.strategyMapping = strategyMapping;
        this.objectMapper = objectMapper;
        this.featureEvaluationService = featureEvaluationService;
    }

    /**
     * Creates a new strategy for the specified feature.
     * Validates the configuration against the strategy type before persisting.
     */
    public StrategyResponse createStrategy(StrategyRequest request) {
        Feature feature = featureRepository.findById(request.id_feature())
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + request.id_feature()));

        validateConfig(request.type(), request.config());

        Strategy strategy = Strategy.builder()
                .type(request.type())
                .config(request.config())
                .active(true)
                .feature(feature)
                .build();

        strategyRepository.save(strategy);
        featureEvaluationService.evictAllEvaluationCaches();
        return strategyMapping.mapStrategyToResponse(strategy);
    }

    /**
     * Retrieves a single strategy by its ID.
     */
    public StrategyResponse getStrategyById(Long id) {
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Strategy not found with id: " + id));
        return strategyMapping.mapStrategyToResponse(strategy);
    }

    /**
     * Retrieves all strategies across all features.
     */
    public List<StrategyResponse> getAllStrategies() {
        return strategyRepository.findAll().stream()
                .map(strategyMapping::mapStrategyToResponse)
                .toList();
    }

    /**
     * Retrieves all strategies belonging to a specific feature.
     */
    public List<StrategyResponse> getStrategiesByFeatureId(Long featureId) {
        if (!featureRepository.existsById(featureId)) {
            throw new ResourceNotFoundException("Feature not found with id: " + featureId);
        }
        return strategyRepository.findByFeatureId(featureId).stream()
                .map(strategyMapping::mapStrategyToResponse)
                .toList();
    }

    /**
     * Updates an existing strategy. Only type and config can be changed;
     * the feature association remains unchanged.
     */
    public StrategyResponse updateStrategy(Long id, StrategyRequest request) {
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Strategy not found with id: " + id));

        validateConfig(request.type(), request.config());

        strategy.setType(request.type());
        strategy.setConfig(request.config());

        Strategy saved = strategyRepository.save(strategy);
        featureEvaluationService.evictAllEvaluationCaches();
        return strategyMapping.mapStrategyToResponse(saved);
    }

    /**
     * Deletes a strategy by its ID.
     */
    public void deleteStrategy(Long id) {
        if (!strategyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Strategy not found with id: " + id);
        }
        strategyRepository.deleteById(id);
        featureEvaluationService.evictAllEvaluationCaches();
    }

    /**
     * Activates a strategy so it participates in feature evaluation.
     */
    public StrategyResponse enableStrategy(Long id) {
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Strategy not found with id: " + id));
        strategy.setActive(true);
        Strategy saved = strategyRepository.save(strategy);
        featureEvaluationService.evictAllEvaluationCaches();
        return strategyMapping.mapStrategyToResponse(saved);
    }

    /**
     * Deactivates a strategy so it is skipped during feature evaluation.
     */
    public StrategyResponse disableStrategy(Long id) {
        Strategy strategy = strategyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Strategy not found with id: " + id));
        strategy.setActive(false);
        Strategy saved = strategyRepository.save(strategy);
        featureEvaluationService.evictAllEvaluationCaches();
        return strategyMapping.mapStrategyToResponse(saved);
    }

    /**
     * Validates the strategy configuration JSON according to the strategy type.
     *
     * <ul>
     *   <li><b>PERCENTAGE</b> — expects a JSON object with a "percentage" field (integer, 0–100)</li>
     *   <li><b>ALLOWLIST</b> — expects a JSON object with a "userIds" field (non-empty array of strings)</li>
     *   <li><b>ENVIRONMENT</b> — expects a JSON object with an "environments" field (non-empty array of strings)</li>
     *   <li><b>DATE</b> — basic valid-JSON check only (reserved for future use)</li>
     * </ul>
     */
    private void validateConfig(StrategyType type, String config) {
        JsonNode root;
        try {
            root = objectMapper.readTree(config);
        } catch (JsonProcessingException e) {
            throw new InvalidStrategyConfigException(
                    "Invalid JSON format in strategy config: " + e.getMessage());
        }

        if (!root.isObject()) {
            throw new InvalidStrategyConfigException(
                    "Strategy config must be a JSON object");
        }

        switch (type) {
            case PERCENTAGE -> {
                JsonNode percentageNode = root.get("percentage");
                if (percentageNode == null || !percentageNode.isInt()) {
                    throw new InvalidStrategyConfigException(
                            "PERCENTAGE strategy requires an integer 'percentage' field");
                }
                int percentage = percentageNode.asInt();
                if (percentage < 0 || percentage > 100) {
                    throw new InvalidStrategyConfigException(
                            "PERCENTAGE must be between 0 and 100, got: " + percentage);
                }
            }
            case ALLOWLIST -> {
                JsonNode userIdsNode = root.get("userIds");
                if (userIdsNode == null || !userIdsNode.isArray() || userIdsNode.isEmpty()) {
                    throw new InvalidStrategyConfigException(
                            "ALLOWLIST strategy requires a non-empty array 'userIds' field");
                }
                for (JsonNode element : userIdsNode) {
                    if (!element.isTextual()) {
                        throw new InvalidStrategyConfigException(
                                "ALLOWLIST 'userIds' must contain only strings");
                    }
                }
            }
            case ENVIRONMENT -> {
                JsonNode environmentsNode = root.get("environments");
                if (environmentsNode == null || !environmentsNode.isArray() || environmentsNode.isEmpty()) {
                    throw new InvalidStrategyConfigException(
                            "ENVIRONMENT strategy requires a non-empty array 'environments' field");
                }
                for (JsonNode element : environmentsNode) {
                    if (!element.isTextual()) {
                        throw new InvalidStrategyConfigException(
                                "ENVIRONMENT 'environments' must contain only strings");
                    }
                }
            }
            case DATE -> {
                // Basic validation only — reserved for future implementation.
                if (root.isEmpty() || root.size() == 0) {
                    throw new InvalidStrategyConfigException(
                            "DATE strategy config cannot be empty");
                }
            }
        }
    }
}

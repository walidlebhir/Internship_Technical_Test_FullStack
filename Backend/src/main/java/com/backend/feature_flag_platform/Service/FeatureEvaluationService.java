package com.backend.feature_flag_platform.Service;

import com.backend.feature_flag_platform.DTO.EvaluationResponse;
import com.backend.feature_flag_platform.Entity.Feature;
import com.backend.feature_flag_platform.Entity.Strategy;
import com.backend.feature_flag_platform.Repository.FeatureRepository;
import com.backend.feature_flag_platform.Repository.StrategyRepository;
import com.backend.feature_flag_platform.evaluation_core.ContextProvider;
import com.backend.feature_flag_platform.evaluation_core.EvaluationContext;
import com.backend.feature_flag_platform.evaluation_core.EvaluationEngine;
import com.backend.feature_flag_platform.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for evaluating whether a feature flag is ON or OFF
 * for a given user and environment.
 * <p>
 * Evaluation rules:
 * <ul>
 *   <li>Feature not found → throw 404</li>
 *   <li>Feature disabled (enabled = false) → OFF</li>
 *   <li>Feature enabled + no active strategies → ON</li>
 *   <li>Feature enabled + active strategies → delegates to {@link EvaluationEngine} (AND logic)</li>
 * </ul>
 */
@Service
public class FeatureEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(FeatureEvaluationService.class);

    private final FeatureRepository featureRepository;
    private final StrategyRepository strategyRepository;
    private final EvaluationEngine evaluationEngine;
    private final ContextProvider contextProvider;

    public FeatureEvaluationService(
            FeatureRepository featureRepository,
            StrategyRepository strategyRepository,
            EvaluationEngine evaluationEngine,
            ContextProvider contextProvider
    ) {
        this.featureRepository = featureRepository;
        this.strategyRepository = strategyRepository;
        this.evaluationEngine = evaluationEngine;
        this.contextProvider = contextProvider;
    }

    /**
     * Evaluates a feature by its unique key and returns an {@link EvaluationResponse}.
     * <p>
     * The cache key combines the feature key, userId, and environment so that
     * the same user in the same environment always gets the cached result.
     * Once a feature or its strategies are modified, the cache is evicted
     * via {@link #evictEvaluationCache(String, String, String)}.
     */
    @Cacheable(
            value = "featureEvaluation",
            key = "#featureKey + '::' + #userId + '::' + #environment",
            unless = "#result == null"
    )
    public EvaluationResponse evaluate(String featureKey, String userId, String environment) {
        Feature feature = findFeatureOrThrow(featureKey);

        if (isFeatureDisabled(feature)) {
            return buildOffResponse(featureKey, userId);
        }

        EvaluationContext context = contextProvider.provide(featureKey, userId, environment);
        List<Strategy> activeStrategies = strategyRepository.findByFeatureIdAndActiveTrue(feature.getId());

        if (activeStrategies.isEmpty()) {
            log.debug("Feature '{}' has no active strategies — returning ON", featureKey);
            return new EvaluationResponse(featureKey, userId, true);
        }

        boolean enabled = evaluationEngine.evaluate(activeStrategies, context);
        log.debug("Feature '{}' evaluated to {}", featureKey, enabled ? "ON" : "OFF");
        return new EvaluationResponse(featureKey, userId, enabled);
    }

    /**
     * Evicts all cached evaluations for a given feature/user/environment combination.
     * Called when a feature or its strategies are modified.
     */
    @CacheEvict(value = "featureEvaluation", allEntries = true)
    public void evictAllEvaluationCaches() {
        log.debug("Evicting all feature evaluation caches");
    }

    /**
     * Finds a feature by key or throws {@link ResourceNotFoundException}.
     */
    private Feature findFeatureOrThrow(String featureKey) {
        return featureRepository.findByKey(featureKey)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Feature not found with key: " + featureKey));
    }

    /**
     * Returns {@code true} if the feature is explicitly disabled.
     */
    private boolean isFeatureDisabled(Feature feature) {
        return Boolean.FALSE.equals(feature.getEnabled());
    }

    /**
     * Builds an OFF response when the feature is disabled.
     */
    private EvaluationResponse buildOffResponse(String featureKey, String userId) {
        log.debug("Feature '{}' is disabled — returning OFF", featureKey);
        return new EvaluationResponse(featureKey, userId, false);
    }
}

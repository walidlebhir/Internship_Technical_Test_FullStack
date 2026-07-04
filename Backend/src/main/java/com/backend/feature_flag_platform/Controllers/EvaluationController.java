package com.backend.feature_flag_platform.Controllers;

import com.backend.feature_flag_platform.DTO.EvaluationResponse;
import com.backend.feature_flag_platform.Service.FeatureEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller dedicated to feature flag evaluation.
 * <p>
 * Separated from the CRUD controller to respect the Single Responsibility Principle.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/features")
public class EvaluationController {

    private final FeatureEvaluationService featureEvaluationService;

    public EvaluationController(FeatureEvaluationService featureEvaluationService) {
        this.featureEvaluationService = featureEvaluationService;
    }

    /**
     * Evaluates whether a feature flag is ON or OFF for a given user and environment.
     * <p>
     * The evaluation applies all active strategies attached to the feature using AND logic.
     *
     * @param key         the unique key of the feature to evaluate
     * @param userId      the identifier of the requesting user (optional)
     * @param environment the current deployment environment (optional)
     * @return {@link EvaluationResponse} containing the result
     */
    @GetMapping("/{key}/evaluate")
    public ResponseEntity<EvaluationResponse> evaluateFeature(
            @PathVariable String key,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String environment) {

        EvaluationResponse response = featureEvaluationService.evaluate(key, userId, environment);
        return ResponseEntity.ok(response);
    }
}

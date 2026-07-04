package com.backend.feature_flag_platform.Controllers;

import com.backend.feature_flag_platform.DTO.StrategyRequest;
import com.backend.feature_flag_platform.DTO.StrategyResponse;
import com.backend.feature_flag_platform.Service.StrategyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StrategyController {

    private final StrategyService strategyService;

    public StrategyController(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    /**
     * Creates a new strategy for a feature.
     * The feature ID is provided in the request body (id_feature).
     */
    @PostMapping("/strategies")
    public ResponseEntity<StrategyResponse> createStrategy(@Valid @RequestBody StrategyRequest request) {
        StrategyResponse response = strategyService.createStrategy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a single strategy by its ID.
     */
    @GetMapping("/strategies/{id}")
    public ResponseEntity<StrategyResponse> getStrategyById(@PathVariable Long id) {
        StrategyResponse response = strategyService.getStrategyById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all strategies across all features.
     */
    @GetMapping("/strategies")
    public ResponseEntity<List<StrategyResponse>> getAllStrategies() {
        List<StrategyResponse> response = strategyService.getAllStrategies();
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all strategies belonging to a specific feature.
     */
    @GetMapping("/features/{featureId}/strategies")
    public ResponseEntity<List<StrategyResponse>> getStrategiesByFeatureId(@PathVariable Long featureId) {
        List<StrategyResponse> response = strategyService.getStrategiesByFeatureId(featureId);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the type and config of an existing strategy.
     */
    @PutMapping("/strategies/{id}")
    public ResponseEntity<StrategyResponse> updateStrategy(
            @PathVariable Long id,
            @Valid @RequestBody StrategyRequest request
    ) {
        StrategyResponse response = strategyService.updateStrategy(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a strategy by its ID.
     */
    @DeleteMapping("/strategies/{id}")
    public ResponseEntity<Void> deleteStrategy(@PathVariable Long id) {
        strategyService.deleteStrategy(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activates a strategy so it is included in evaluation.
     */
    @PatchMapping("/strategies/{id}/enable")
    public ResponseEntity<StrategyResponse> enableStrategy(@PathVariable Long id) {
        StrategyResponse response = strategyService.enableStrategy(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivates a strategy so it is skipped during evaluation.
     */
    @PatchMapping("/strategies/{id}/disable")
    public ResponseEntity<StrategyResponse> disableStrategy(@PathVariable Long id) {
        StrategyResponse response = strategyService.disableStrategy(id);
        return ResponseEntity.ok(response);
    }
}

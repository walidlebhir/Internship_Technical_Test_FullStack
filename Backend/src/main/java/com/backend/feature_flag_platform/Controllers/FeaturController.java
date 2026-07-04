package com.backend.feature_flag_platform.Controllers;

import com.backend.feature_flag_platform.DTO.FeatureRequest;
import com.backend.feature_flag_platform.DTO.FeatureResponse;
import com.backend.feature_flag_platform.Service.FeatureService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/features")
public class FeaturController {

    private final FeatureService featureService;

    public FeaturController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @PostMapping
    public ResponseEntity<FeatureResponse> createFeature(@Valid @RequestBody FeatureRequest request) {
        FeatureResponse response = featureService.createFeature(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeatureResponse> getFeatureById(@PathVariable Long id) {
        FeatureResponse response = featureService.getFeatureById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FeatureResponse>> getAllFeatures() {
        List<FeatureResponse> response = featureService.getAllFeatures();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeatureResponse> updateFeature(
            @PathVariable Long id, @Valid @RequestBody FeatureRequest request) {
        FeatureResponse response = featureService.updateFeature(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeature(@PathVariable Long id) {
        featureService.deleteFeature(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<FeatureResponse> enableFeature(@PathVariable Long id) {
        FeatureResponse response = featureService.enableFeature(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<FeatureResponse> disableFeature(@PathVariable Long id) {
        FeatureResponse response = featureService.disableFeature(id);
        return ResponseEntity.ok(response);
    }
}

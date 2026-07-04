package com.backend.feature_flag_platform.MappedStructer;

import com.backend.feature_flag_platform.DTO.FeatureResponse;
import com.backend.feature_flag_platform.Entity.Feature;
import org.springframework.stereotype.Component;

@Component
public class FeatureMapping {

    public FeatureResponse mapFeatureToResponse(Feature feature) {
        return new FeatureResponse(
                feature.getId(),
                feature.getKey(),
                feature.getDescription(),
                feature.getEnabled(),
                feature.getDomain().getId(),
                feature.getCreatedAt(),
                feature.getUpdatedAt()
        );
    }
}

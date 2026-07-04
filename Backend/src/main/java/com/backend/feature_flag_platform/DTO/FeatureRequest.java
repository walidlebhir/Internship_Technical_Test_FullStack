package com.backend.feature_flag_platform.DTO;

import java.util.UUID;

public record FeatureRequest(
        String key ,
        String description ,
        UUID id_domain

) {
}

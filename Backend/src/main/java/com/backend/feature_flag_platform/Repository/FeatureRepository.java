package com.backend.feature_flag_platform.Repository;

import com.backend.feature_flag_platform.Entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {
    Optional<Feature> findByKey(String key);
}

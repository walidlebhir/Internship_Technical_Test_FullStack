package com.backend.feature_flag_platform.Repository;

import com.backend.feature_flag_platform.Entity.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Long> {

    /**
     * Retrieves all strategies belonging to a specific feature.
     */
    List<Strategy> findByFeatureId(Long featureId);

    /**
     * Retrieves only the active strategies for a given feature.
     * Used by the evaluation engine to fetch strategies that should be evaluated.
     */
    List<Strategy> findByFeatureIdAndActiveTrue(Long featureId);
}

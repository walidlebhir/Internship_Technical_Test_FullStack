package com.backend.feature_flag_platform.Repository;

import com.backend.feature_flag_platform.Entity.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Long> {

    List<Strategy> findByFeatureId(Long featureId);

    List<Strategy> findByFeatureIdAndActiveTrue(Long featureId);
}

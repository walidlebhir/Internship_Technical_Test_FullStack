package com.backend.feature_flag_platform.Repository;

import com.backend.feature_flag_platform.Entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DomainRepository extends JpaRepository<Domain , UUID > {
    Domain findByName(String name ) ;
}

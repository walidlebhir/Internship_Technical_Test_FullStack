package com.backend.feature_flag_platform.MappedStructer;

import com.backend.feature_flag_platform.DTO.DomainResponse;
import com.backend.feature_flag_platform.Entity.Domain;
import org.springframework.stereotype.Component;

@Component
public class DomainMapping {

    public DomainResponse mapDomainToResponse(Domain domain) {
        return new DomainResponse(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getCreatedAt()
        );
    }

}

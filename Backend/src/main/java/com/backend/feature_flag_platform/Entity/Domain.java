package com.backend.feature_flag_platform.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "domain")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Domain {

    @Id
    @UuidGenerator
    private UUID id;

    private  String name ;

    private String description ;


    // relation OneTomany with feature :
    @OneToMany(
            mappedBy = "domain",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Feature> features = new ArrayList<>();


    @CreationTimestamp
    private LocalDateTime CreatedAt ;

    @UpdateTimestamp
    private  LocalDateTime UpdatedAt ;
}

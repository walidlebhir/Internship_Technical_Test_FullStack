package com.backend.feature_flag_platform.Entity;


import com.backend.feature_flag_platform.Entity.Enum.StrategyType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "strategy")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Strategy {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private  Long id ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StrategyType type;

    @Lob
    @Column(nullable = false)
    private String config;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

}

package com.backend.feature_flag_platform.Entity;


import com.backend.feature_flag_platform.Entity.Enum.AuditAction;
import com.backend.feature_flag_platform.Entity.Enum.EntityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditentry")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuditEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType entityType;

    @Column(nullable = false)
    private String entityId;

    @Column(nullable = false)
    private String who;

    @PrePersist
    void onCreate() {
        timestamp = LocalDateTime.now();
    }
}

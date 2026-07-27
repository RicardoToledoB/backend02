package com.cosam.project01.demand.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(
        name = "biopsychosocial_commitment_levels",
        indexes = {
                @Index(name = "idx_biopsychosocial_commitment_levels_code", columnList = "code"),
                @Index(name = "idx_biopsychosocial_commitment_levels_active", columnList = "active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BiopsychosocialCommitmentLevelEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 80)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    private Boolean active;

    @PrePersist
    private void onCreate() {
        if (this.active == null) this.active = true;
    }
}

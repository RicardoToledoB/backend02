package com.cosam.project01.demand.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(
        name = "citation_types",
        indexes = {
                @Index(name = "idx_citation_types_code", columnList = "code"),
                @Index(name = "idx_citation_types_active_sort", columnList = "active, sort_order")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CitationTypeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 120)
    private String code;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(name = "sort_order")
    private Integer sortOrder;

    private Boolean active;

    @PrePersist
    private void onCreate() {
        if (this.active == null) this.active = true;
    }
}

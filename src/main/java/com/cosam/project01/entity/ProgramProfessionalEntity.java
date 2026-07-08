package com.cosam.project01.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "program_professionals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE program_professionals SET deleted_at = CURRENT_TIMESTAMP, active = false WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class ProgramProfessionalEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profession_id", nullable = false)
    private ProfessionEntity profession;

    @Column(length = 180)
    private String email;

    @Column(length = 60)
    private String phone;

    @Column(length = 500)
    private String observation;

    @Builder.Default
    private Boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Builder.Default
    @OneToMany(mappedBy = "programProfessional", cascade = CascadeType.ALL, orphanRemoval = false)
    @Where(clause = "deleted_at IS NULL")
    private List<ProgramProfessionalProgramEntity> programLinks = new ArrayList<>();

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.active == null) this.active = true;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

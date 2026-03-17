package com.cosam.project01.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="registers")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@SQLDelete(sql = "UPDATE registers SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class RegisterEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="postulant_id")
    private PostulantEntity postulant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="contact_type_id")
    private ContactTypeEntity contactType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sender_id")
    private SenderEntity sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="diverter_id")
    private DiverterEntity diverter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="program_id")
    private ProgramEntity program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="result_id")
    private ResultEntity result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="not_relevant_id")
    private NotRelevantEntity notRelevant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="state_id")
    private StateEntity state;

    private String date_attention;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="contact")
    private ContactEntity contact;

    private String is_history;

    private String number_tto;

    private String date_close1;
    private String date_close2;
    private String date_close3;
    private String date_state1;
    private String date_state2;
    private String date_state3;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    private void createdAt(){
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void updatedAt(){
        this.updatedAt = LocalDateTime.now();
    }


}

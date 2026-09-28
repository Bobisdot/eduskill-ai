package com.eduskill.eduskill_ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @Column(length = 1000)
    private String coverLetter; // Студенттің ілеспе хаты (резюме сілтемесі немесе хабарламасы)

    private String status; // PENDING, ACCEPTED, REJECTED

    private LocalDateTime appliedAt;

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }
}
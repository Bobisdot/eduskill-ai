package com.eduskill.eduskill_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Студенттің негізгі аккаунтымен байланыс
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    private String university;

    private String major;

    // Мысалы: "Java, Spring Boot, AWS, Python"
    private String skills;

    private String githubUrl;

    private String leetcodeUrl;

    private String codeforcesUrl;

    private String otherLinks;

    // Жоба немесе хакатон үшін команда іздеп жүр ме?
    @Column(name = "is_looking_for_team", columnDefinition = "boolean default false")
    private boolean isLookingForTeam;

    @Column(length = 500)
    private String bio;
}
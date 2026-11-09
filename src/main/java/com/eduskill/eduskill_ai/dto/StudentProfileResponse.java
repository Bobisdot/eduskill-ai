package com.eduskill.eduskill_ai.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StudentProfileResponse {
    private Integer id;
    private Long userId;
    private String userEmail;
    private String firstName;
    private String lastName;
    private String university;
    private String major;
    private String skills;
    private String githubUrl;
    private String leetcodeUrl;
    private String codeforcesUrl;
    private String otherLinks;
    private String bio;
    private boolean isLookingForTeam;


    private Integer currentStreak;
    private LocalDate lastActiveDate;
}
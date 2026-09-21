package com.eduskill.eduskill_ai.dto;

import lombok.Data;

@Data
public class StudentProfileRequest {
    private String university;
    private String major;
    private String skills;
    private String githubUrl;
    private String leetcodeUrl;
    private String codeforcesUrl;
    private String otherLinks;
    private boolean isLookingForTeam;
    private String bio;
}
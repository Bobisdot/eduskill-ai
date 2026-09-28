package com.eduskill.eduskill_ai.dto;

import lombok.Data;

@Data
public class ProjectRequest {
    private String title;
    private String description;
    private String requiredSkills; // Мысалы: "Java, Spring Boot, MySQL"
    private String type;           // "INTERNSHIP" немесе "PET_PROJECT"
}
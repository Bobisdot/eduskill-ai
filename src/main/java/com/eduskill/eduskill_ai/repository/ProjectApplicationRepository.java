package com.eduskill.eduskill_ai.repository;

import com.eduskill.eduskill_ai.entity.ProjectApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long> {

    // Студенттің бұған дейін тапсырған өтінімдері
    List<ProjectApplication> findByApplicantId(Long applicantId);

    // Нақты проектке/стажировкаға түскен барлық өтінімдер (Компания көруі үшін)
    List<ProjectApplication> findByProjectId(Long projectId);

    // Студент бір проектке екі рет өтінім жібермес үшін тексеру
    boolean existsByProjectIdAndApplicantId(Long projectId, Long applicantId);
}
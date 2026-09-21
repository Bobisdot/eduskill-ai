package com.eduskill.eduskill_ai.repository;

import com.eduskill.eduskill_ai.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Integer> {

    // Осында Integer орнына Long жазамыз
    Optional<StudentProfile> findByUserId(Long userId);

    List<StudentProfile> findByIsLookingForTeamTrue();
}
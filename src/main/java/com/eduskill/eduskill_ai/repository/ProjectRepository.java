package com.eduskill.eduskill_ai.repository;

import com.eduskill.eduskill_ai.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Стажировкаларды немесе Pet-проекттерді бөлек сүзгілеп алу
    List<Project> findByType(String type);
}

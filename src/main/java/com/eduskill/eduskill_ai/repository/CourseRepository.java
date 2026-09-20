package com.eduskill.eduskill_ai.repository;

import com.eduskill.eduskill_ai.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
}
package com.eduskill.eduskill_ai.service;

import com.eduskill.eduskill_ai.dto.CourseRequest;
import com.eduskill.eduskill_ai.entity.Course;
import com.eduskill.eduskill_ai.entity.User;
import com.eduskill.eduskill_ai.repository.CourseRepository;
import com.eduskill.eduskill_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public Course createCourse(CourseRequest request, String userEmail) {
        User instructor = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Қолданушы табылмады"));

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .instructor(instructor)
                .build();

        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}
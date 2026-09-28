package com.eduskill.eduskill_ai.service;

import com.eduskill.eduskill_ai.dto.ProjectRequest;
import com.eduskill.eduskill_ai.entity.Project;
import com.eduskill.eduskill_ai.entity.User;
import com.eduskill.eduskill_ai.repository.ProjectRepository;
import com.eduskill.eduskill_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // Жаңа стажировка/проект қосу (Тек COMPANY немесе MENTOR үшін)
    public Project createProject(ProjectRequest request, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Қолданушы табылмады"));

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .requiredSkills(request.getRequiredSkills())
                .type(request.getType())
                .owner(owner)
                .build();

        return projectRepository.save(project);
    }

    // Барлық бар проекттерді алу (Студенттерге де, компанияларға да)
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // Түрлері бойынша сүзгілеу (INTERNSHIP немесе PET_PROJECT)
    public List<Project> getProjectsByType(String type) {
        return projectRepository.findByType(type);
    }
}
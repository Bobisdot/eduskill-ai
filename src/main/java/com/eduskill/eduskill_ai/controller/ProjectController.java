package com.eduskill.eduskill_ai.controller;

import com.eduskill.eduskill_ai.dto.ProjectRequest;
import com.eduskill.eduskill_ai.entity.Project;
import com.eduskill.eduskill_ai.entity.ProjectApplication;
import com.eduskill.eduskill_ai.entity.User;
import com.eduskill.eduskill_ai.repository.ProjectApplicationRepository;
import com.eduskill.eduskill_ai.repository.ProjectRepository;
import com.eduskill.eduskill_ai.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final ProjectApplicationRepository applicationRepository;

    // 1. Жаңа проект/стажировка жариялау
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody ProjectRequest request, Authentication authentication) {
        return ResponseEntity.ok(projectService.createProject(request, authentication.getName()));
    }

    // 2. Барлық проекттер тізімін алу
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // 3. Түріне қарай сүзгілеп алу ( /api/v1/projects/type/INTERNSHIP )
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Project>> getProjectsByType(@PathVariable String type) {
        return ResponseEntity.ok(projectService.getProjectsByType(type));
    }

    // 4. Студенттің стажировкаға/проектке өтінім жіберуі
    @PostMapping("/{projectId}/apply")
    public ResponseEntity<?> applyForProject(
            @PathVariable Long projectId,
            @RequestBody(required = false) Map<String, String> request,
            Authentication authentication) {

        User student = (User) authentication.getPrincipal();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Проект табылмады!"));

        if (applicationRepository.existsByProjectIdAndApplicantId(projectId, student.getId())) {
            return ResponseEntity.badRequest().body("Сіз бұл стажировкаға өтінім беріп қойғансыз!");
        }

        String coverLetter = (request != null && request.containsKey("coverLetter"))
                ? request.get("coverLetter")
                : "";

        ProjectApplication application = ProjectApplication.builder()
                .project(project)
                .applicant(student)
                .coverLetter(coverLetter)
                .build();

        applicationRepository.save(application);

        return ResponseEntity.ok("Өтінім сәтті жіберілді!");
    }
    // 1. Компания өкілі өз стажировкасына түскен өтінімдерді көреді
    @GetMapping("/{projectId}/applications")
    public ResponseEntity<List<ProjectApplication>> getApplicationsForProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(applicationRepository.findByProjectId(projectId));
    }

    // 2. Студент өзі берген өтінімдерді көреді
    @GetMapping("/my-applications")
    public ResponseEntity<List<ProjectApplication>> getMyApplications(Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        return ResponseEntity.ok(applicationRepository.findByApplicantId(student.getId()));
    }
}
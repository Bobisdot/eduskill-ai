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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final ProjectApplicationRepository applicationRepository;

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody ProjectRequest request, Authentication authentication) {
        return ResponseEntity.ok(projectService.createProject(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Project>> getProjectsByType(@PathVariable String type) {
        return ResponseEntity.ok(projectService.getProjectsByType(type));
    }

    @PostMapping("/{projectId}/apply")
    public ResponseEntity<?> applyForProject(
            @PathVariable Long projectId,
            @RequestBody(required = false) Map<String, String> request,
            Authentication authentication) {

        User student = (User) authentication.getPrincipal();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Проект табылмады!"));

        if (applicationRepository.existsByProjectIdAndApplicantId(projectId, student.getId())) {
            return ResponseEntity.badRequest().body("Сіз бұл проектке өтінім беріп қойғансыз!");
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

    @GetMapping("/{projectId}/applications")
    public ResponseEntity<List<ProjectApplication>> getApplicationsForProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(applicationRepository.findByProjectId(projectId));
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<ProjectApplication>> getMyApplications(Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        return ResponseEntity.ok(applicationRepository.findByApplicantId(student.getId()));
    }

    @PostMapping("/applications/{applicationId}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam String status,
            Authentication authentication) {

        ProjectApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Өтінім табылмады!"));

        User currentUser = (User) authentication.getPrincipal();

        if (!application.getProject().getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest().body("Сізде бұл өтінімнің мәртебесін өзгертуге рұқсат жоқ!");
        }

        application.setStatus(status);
        applicationRepository.save(application);

        return ResponseEntity.ok("Өтінім мәртебесі жаңартылды: " + status);
    }

    // 8. Өзі жасаған жобалар тізімі (Автор)
    @GetMapping("/my-created")
    public ResponseEntity<List<Project>> getMyCreatedProjects(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(projectRepository.findAll().stream()
                .filter(p -> p.getOwner() != null && p.getOwner().getId().equals(user.getId()))
                .collect(Collectors.toList()));
    }

    // 9. Өтінімі ҚАБЫЛДАНҒАН (ACCEPTED) Жобалар тізімі (Портфолио/Команда мүшесі)
    @GetMapping("/my-accepted")
    public ResponseEntity<List<Project>> getMyAcceptedProjects(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<ProjectApplication> myApps = applicationRepository.findByApplicantId(user.getId());

        List<Project> acceptedProjects = myApps.stream()
                .filter(app -> "ACCEPTED".equalsIgnoreCase(app.getStatus()))
                .map(ProjectApplication::getProject)
                .collect(Collectors.toList());

        return ResponseEntity.ok(acceptedProjects);
    }
}
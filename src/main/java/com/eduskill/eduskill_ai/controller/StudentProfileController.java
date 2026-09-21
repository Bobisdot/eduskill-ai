package com.eduskill.eduskill_ai.controller;

import com.eduskill.eduskill_ai.dto.StudentProfileRequest;
import com.eduskill.eduskill_ai.entity.StudentProfile;
import com.eduskill.eduskill_ai.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List; // Импорттауды ұмытпаңыз

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService profileService;

    @PostMapping
    public ResponseEntity<StudentProfile> createOrUpdateProfile(
            @RequestBody StudentProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(profileService.createOrUpdateProfile(request, userDetails.getUsername()));
    }

    @GetMapping("/me")
    public ResponseEntity<StudentProfile> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(profileService.getMyProfile(userDetails.getUsername()));
    }



    // ... алдыңғы кодтар ...

    @GetMapping("/team-hunters")
    public ResponseEntity<List<StudentProfile>> getTeamHunters(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(profileService.getStudentsLookingForTeam(userDetails.getUsername()));
    }
}
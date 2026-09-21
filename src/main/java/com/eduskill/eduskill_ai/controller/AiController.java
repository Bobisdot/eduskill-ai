package com.eduskill.eduskill_ai.controller;


import com.eduskill.eduskill_ai.service.AiRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiRecommendationService aiService;

    @GetMapping("/recommend-courses")
    public ResponseEntity<String> getRecommendations(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(aiService.getCourseRecommendations(userDetails.getUsername()));
    }
}
package com.eduskill.eduskill_ai.controller;

import com.eduskill.eduskill_ai.dto.AuthenticationRequest;
import com.eduskill.eduskill_ai.dto.AuthenticationResponse;
import com.eduskill.eduskill_ai.dto.UserRegistrationRequest;
import com.eduskill.eduskill_ai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(userService.authenticate(request));
    }
}
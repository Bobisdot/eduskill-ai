package com.eduskill.eduskill_ai.service;


import com.eduskill.eduskill_ai.dto.AuthenticationRequest;
import com.eduskill.eduskill_ai.dto.AuthenticationResponse;
import com.eduskill.eduskill_ai.dto.UserRegistrationRequest;
import com.eduskill.eduskill_ai.entity.User;
import com.eduskill.eduskill_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.eduskill.eduskill_ai.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;

@Service
@RequiredArgsConstructor

public class UserService {



    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Бұл email тіркеліп қойған!");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);
        return "Пайдаланушы сәтті тіркелді!";
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. Пайдаланушының логин/паролін тексеру
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Базадан пайдаланушыны табу
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Пайдаланушы табылмады"));

        // 3. Токен жасау
        var jwtToken = jwtService.generateToken(user);

        // 4. Жауапты қайтару
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

}
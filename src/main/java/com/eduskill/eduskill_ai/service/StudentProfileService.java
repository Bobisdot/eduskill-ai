package com.eduskill.eduskill_ai.service;

import com.eduskill.eduskill_ai.dto.StudentProfileRequest;
import com.eduskill.eduskill_ai.entity.StudentProfile;
import com.eduskill.eduskill_ai.entity.User;
import com.eduskill.eduskill_ai.repository.StudentProfileRepository;
import com.eduskill.eduskill_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;

    public StudentProfile createOrUpdateProfile(StudentProfileRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Қолданушы табылмады"));

        StudentProfile profile = profileRepository.findByUserId(user.getId())
                .orElse(new StudentProfile());

        profile.setUser(user);
        profile.setUniversity(request.getUniversity());
        profile.setMajor(request.getMajor());
        profile.setSkills(request.getSkills());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setLeetcodeUrl(request.getLeetcodeUrl());
        profile.setCodeforcesUrl(request.getCodeforcesUrl());
        profile.setOtherLinks(request.getOtherLinks());
        profile.setLookingForTeam(request.isLookingForTeam());
        profile.setBio(request.getBio());

        return profileRepository.save(profile);
    }

    public StudentProfile getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Қолданушы табылмады"));

        return profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Профиль әлі толтырылмаған"));
    }

    public List<StudentProfile> getStudentsLookingForTeam(String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Қолданушы табылмады"));

        // Команда іздеп жүрген барлық студенттерді базадан алу
        List<StudentProfile> allLooking = profileRepository.findByIsLookingForTeamTrue();

        // Тізімнен өз-өзімізді алып тастаймыз (өзімізге өзіміз команда болмаймыз ғой)
        allLooking.removeIf(profile -> profile.getUser().getId().equals(currentUser.getId()));

        return allLooking;
    }
}
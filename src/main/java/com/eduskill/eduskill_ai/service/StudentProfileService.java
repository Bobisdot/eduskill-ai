package com.eduskill.eduskill_ai.service;

import com.eduskill.eduskill_ai.dto.StudentProfileRequest;
import com.eduskill.eduskill_ai.dto.StudentProfileResponse;
import com.eduskill.eduskill_ai.entity.StudentProfile;
import com.eduskill.eduskill_ai.entity.User;
import com.eduskill.eduskill_ai.repository.StudentProfileRepository;
import com.eduskill.eduskill_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        // Профильді жаңартқанда да стрик есептеледі
        updateStreakLogic(profile);

        return profileRepository.save(profile);
    }

    public StudentProfile getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Қолданушы табылмады"));

        StudentProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Профиль әлі толтырылмаған"));

        // Профильге кірген сайын күнделікті стрик жаңартылып, базаға сақталады
        updateStreakLogic(profile);
        return profileRepository.save(profile);
    }

    public List<StudentProfile> getStudentsLookingForTeam(String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Қолданушы табылмады"));

        List<StudentProfile> allLooking = profileRepository.findByIsLookingForTeamTrue();
        allLooking.removeIf(profile -> profile.getUser().getId().equals(currentUser.getId()));

        return allLooking;
    }

    public StudentProfileResponse getMyProfileResponse(String email) {
        StudentProfile profile = getMyProfile(email);
        return StudentProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .userEmail(profile.getUser().getEmail())
                .firstName(profile.getUser().getFirstName())
                .lastName(profile.getUser().getLastName())
                .university(profile.getUniversity())
                .major(profile.getMajor())
                .skills(profile.getSkills())
                .githubUrl(profile.getGithubUrl())
                .leetcodeUrl(profile.getLeetcodeUrl())
                .codeforcesUrl(profile.getCodeforcesUrl())
                .otherLinks(profile.getOtherLinks())
                .bio(profile.getBio())
                .isLookingForTeam(profile.isLookingForTeam())
                .currentStreak(profile.getCurrentStreak())
                .lastActiveDate(profile.getLastActiveDate())
                .build();
    }

    public StudentProfile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Студент профилі табылмады!"));
    }

    // 🔥 Day Streak есептеу алгоритмі
    private void updateStreakLogic(StudentProfile profile) {
        LocalDate today = LocalDate.now();
        LocalDate lastActive = profile.getLastActiveDate();

        if (profile.getCurrentStreak() == null) {
            profile.setCurrentStreak(0);
        }

        if (lastActive == null) {
            // Бірінші рет белсенді болып тұр
            profile.setCurrentStreak(1);
            profile.setLastActiveDate(today);
        } else if (lastActive.equals(today.minusDays(1))) {
            // Кеше де кірген — стрик өседі
            profile.setCurrentStreak(profile.getCurrentStreak() + 1);
            profile.setLastActiveDate(today);
        } else if (!lastActive.equals(today)) {
            // 1 күннен көп өтіп кетсе — стрик күйіп, кайта 1 болады
            profile.setCurrentStreak(1);
            profile.setLastActiveDate(today);
        }
        // Егер кеше сақталған болып, бүгін қайта кірсе — lastActive бұрыннан today, стрик өзгермейді
    }
}
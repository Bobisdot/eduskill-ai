package com.eduskill.eduskill_ai.service;

import com.eduskill.eduskill_ai.entity.StudentProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {

    private final StudentProfileService profileService;
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String getCourseRecommendations(String email) {
        // 1. Получение профиля студента из базы
        StudentProfile profile = profileService.getMyProfile(email);

        // 2. Формирование промпта
        String promptText = String.format(
                "I am an IT student studying %s. My current skills are: %s. " +
                        "Please provide a personalized learning roadmap and recommend 3 best courses (Udemy, Coursera, or YouTube) for my career growth.",
                profile.getMajor(), profile.getSkills()
        );

        // 3. Формирование тела JSON-запроса
        Map<String, Object> textMap = Map.of("text", promptText);
        Map<String, Object> partsMap = Map.of("parts", List.of(textMap));
        Map<String, Object> requestBody = Map.of("contents", List.of(partsMap));

        // 4. Отправка POST-запроса к API
        String fullUrl = apiUrl + "?key=" + apiKey;

        try {
            Map response = restTemplate.postForObject(fullUrl, requestBody, Map.class);

            if (response != null && response.containsKey("candidates")) {
                List candidates = (List) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map firstCandidate = (Map) candidates.get(0);
                    Map content = (Map) firstCandidate.get("content");
                    List parts = (List) content.get("parts");
                    Map firstPart = (Map) parts.get(0);
                    return (String) firstPart.get("text");
                }
            }
            return "Ответ от Gemini получен, но структуру данных не удалось распарсить.";
        } catch (Exception e) {
            return "Ошибка при вызове Gemini API: " + e.getMessage();
        }
    }
}
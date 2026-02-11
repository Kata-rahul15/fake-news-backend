package com.fakenews.service;

import com.fakenews.dto.Evidence;
import com.fakenews.dto.PredictResponse;
import com.fakenews.model.History;
import com.fakenews.model.User;
import com.fakenews.repository.HistoryRepository;
import com.fakenews.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PredictService {

    private final UserRepository userRepository;

    // ✅ ADDED (needed to save domain)
    private final HistoryRepository historyRepository;
    private final HistoryService historyService;

    @Autowired
    private BadgeService badgeService;


    private final RestTemplate restTemplate = new RestTemplate();

    // 🔹 FastAPI base URL
    private static final String ML_URL = "http://localhost:8000";

    // ===============================
    // 🔵 TEXT PREDICTION
    // ===============================
    public PredictResponse predictFromText(String text, String token, Authentication auth) {

        if (text == null || text.isBlank()) {
            return emptyResponse("No text provided.");
        }

        Map<String, Object> body = Map.of("text", text);

        Map<String, Object> response = restTemplate.postForObject(
                ML_URL + "/predict",
                body,
                Map.class
        );

        System.out.println("====== ML TEXT API RESPONSE ======");
        System.out.println(response);
        System.out.println("================================");

        updateImpactScore(auth); // 🔥 IMPACT UPDATE

        PredictResponse res = mapToResponse(response);

        // ✅ SAVE HISTORY (NO DOMAIN FOR TEXT)
        saveHistory(auth, text, res, null);

        return res;
    }

    // ===============================
    // 🟣 URL PREDICTION
    // ===============================
    public PredictResponse predictFromUrl(String url, String token, Authentication auth) {

        if (url == null || url.isBlank()) {
            return emptyResponse("No URL provided.");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("url", url);

        Map<String, Object> response = restTemplate.postForObject(
                ML_URL + "/predict_url",
                body,
                Map.class
        );

        System.out.println("====== ML URL API RESPONSE ======");
        System.out.println(response);
        System.out.println("===============================");

        updateImpactScore(auth); // 🔥 IMPACT UPDATE

        PredictResponse res = mapToResponse(response);

        // ✅ EXTRACT DOMAIN
        String domain = extractDomain(url);

        // ✅ SAVE HISTORY WITH DOMAIN
        saveHistory(auth, url, res, domain);

        return res;
    }

    // ===============================
    // 🟢 IMAGE (OCR) PREDICTION
    // ===============================
    public PredictResponse predictFromImage(String base64Image, String token, Authentication auth) {

        if (base64Image == null || base64Image.isBlank()) {
            return emptyResponse("No image provided.");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("image_base64", base64Image);

        Map<String, Object> response = restTemplate.postForObject(
                ML_URL + "/predict_image",
                body,
                Map.class
        );

        System.out.println("====== ML IMAGE API RESPONSE ======");
        System.out.println(response);
        System.out.println("=================================");

        updateImpactScore(auth); // 🔥 IMPACT UPDATE

        PredictResponse res = mapToResponse(response);

        // ✅ SAVE HISTORY (NO DOMAIN FOR IMAGE)
        saveHistory(auth, "[IMAGE]", res, null);

        return res;
    }

    // ===============================
    // 🔥 SAVE HISTORY (CENTRALIZED)
    // ===============================
    private void saveHistory(Authentication auth, String text, PredictResponse res, String domain) {

        User user = null;
        if (auth != null && auth.isAuthenticated()) {
            user = userRepository.findByEmail(auth.getName()).orElse(null);
        }

        // IMPORTANT:
        // finalLabel MUST be: real / fake / unverifiable
        History h = historyService.save(
                user,
                text,
                res.getFinalLabel(),
                res.getConfidence(),
                res.getExplanation()
        );

        // ✅ SAVE DOMAIN ONLY IF PRESENT
        if (domain != null) {
            h.setDomain(domain);
            historyRepository.save(h);
        }

        System.out.println("SAVED HISTORY → label=" + res.getFinalLabel()
                + " | domain=" + domain);
    }

    // ===============================
    // 🌐 DOMAIN EXTRACTION (SAFE)
    // ===============================
    private String extractDomain(String url) {
        try {
            if (url == null || !url.startsWith("http")) return null;
            URI uri = new URI(url);
            return uri.getHost(); // example: fakeworld.net
        } catch (Exception e) {
            return null;
        }
    }

    // ===============================
    // 🔥 IMPACT SCORE UPDATE
    // ===============================
    private void updateImpactScore(Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return;
        }

        String email = auth.getName();

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return;

        user.setChecksPerformed(user.getChecksPerformed() + 1);
        user.setKarmaPoints(user.getKarmaPoints() + 2);
        user.setBadge(badgeService.calculateBadge(user.getKarmaPoints()));

        userRepository.save(user);


        System.out.println("Impact updated for user: " + email +
                " | KarmaPoints = " + user.getKarmaPoints());
    }

    // ===============================
    // 🔹 MAP EVIDENCE LIST → DTO
    // ===============================
    @SuppressWarnings("unchecked")
    private List<Evidence> mapEvidence(Object evidenceObj) {

        if (!(evidenceObj instanceof List<?> evidenceList)) {
            return List.of();
        }

        return evidenceList.stream()
                .filter(e -> e instanceof Map)
                .map(e -> {
                    Map<String, Object> ev = (Map<String, Object>) e;

                    return Evidence.builder()
                            .source((String) ev.get("source"))
                            .url((String) ev.get("url"))
                            .title((String) ev.get("title"))
                            .snippet((String) ev.get("snippet"))
                            .build();
                })
                .toList();
    }

    // ===============================
    // 🔶 MAP JSON → DTO
    // ===============================
    private PredictResponse mapToResponse(Map<String, Object> map) {

        if (map == null || map.isEmpty()) {
            return emptyResponse("No response from ML service.");
        }

        if (map.containsKey("success") && Boolean.FALSE.equals(map.get("success"))) {
            return emptyResponse("Unable to analyze content.");
        }

        return PredictResponse.builder()
                .finalLabel(
                        map.containsKey("finalLabel")
                                ? (String) map.get("finalLabel")
                                : (String) map.getOrDefault("status", "UNKNOWN")
                )
                .confidence(
                        map.containsKey("confidencePercent")
                                ? toDouble(map.get("confidencePercent"))
                                : toDouble(map.getOrDefault("confidence", 0.0))
                )
                .summary((String) map.get("summary"))
                .explanation(
                        map.containsKey("aiExplanation")
                                ? (String) map.get("aiExplanation")
                                : (String) map.get("explanation")
                )
                .language((String) map.getOrDefault("language", "unknown"))
                .keywords(
                        map.get("keywords") instanceof List
                                ? (List<String>) map.get("keywords")
                                : List.of()
                )
                .sentiment(
                        map.get("sentiment") instanceof Map
                                ? (Map<String, Object>) map.get("sentiment")
                                : null
                )
                .factCheckUsed((Boolean) map.getOrDefault("factCheckUsed", false))
                .factCheckSource((String) map.get("factCheckSource"))
                .verificationMethod((String) map.getOrDefault("verificationMethod", "NONE"))
                .evidence(mapEvidence(map.get("evidence")))
                .build();
    }

    // ===============================
    // 🧱 SAFE EMPTY RESPONSE
    // ===============================
    private PredictResponse emptyResponse(String message) {
        return PredictResponse.builder()
                .finalLabel("UNKNOWN")
                .confidence(0.0)
                .sentiment(null)
                .keywords(List.of())
                .language("unknown")
                .summary(null)
                .factCheckUsed(false)
                .factCheckSource(null)
                .verificationMethod("NONE")
                .explanation(message)
                .evidence(List.of())
                .build();
    }

    // ===============================
    // 🔢 SAFE DOUBLE PARSER
    // ===============================
    private double toDouble(Object value) {
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }


}

package com.nagardrishti.service;

import com.nagardrishti.entity.Scheme;
import com.nagardrishti.entity.User;
import com.nagardrishti.dto.EligibleSchemesResponse;
import com.nagardrishti.repository.SchemeRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchemeService {

    private final SchemeRepository schemeRepository;
    private final jakarta.persistence.EntityManager entityManager;

    public SchemeService(SchemeRepository schemeRepository, jakarta.persistence.EntityManager entityManager) {
        this.schemeRepository = schemeRepository;
        this.entityManager = entityManager;
    }

    /**
     * Resolves type mismatch error by mapping matching profiles directly into the
     * EligibleSchemesResponse DTO layout along with summary metrics.
     */
    public EligibleSchemesResponse getEligibleSchemes(String userId) {
        User user = entityManager.find(User.class, userId);

        // 1. Fetch filtered and prioritized options using optimized loop processing
        List<Scheme> sortedSchemes = (user == null) ? Collections.emptyList() : getEligibleSchemesForUser(user);

        // 2. Compute dynamic aggregate benefits metrics safely from descriptive strings
        double aggregateBenefit = sortedSchemes.stream()
                .mapToDouble(scheme -> parseBenefitAmount(scheme.getBenefitAmount()))
                .sum();

        // 3. Assemble and return using the DTO Builder
        return EligibleSchemesResponse.builder()
                .totalEligibleSchemes(sortedSchemes.size())
                .totalPotentialBenefit(aggregateBenefit)
                .schemes(sortedSchemes)
                .build();
    }

    /**
     * Resolves error: cannot find symbol method getSchemeById(java.lang.String)
     */

    public Scheme getSchemeById(String id) {
        return schemeRepository.findById(id).orElse(null);
    }

    /**
     * Resolves error: cannot find symbol method search(java.lang.String)
     */
    public List<Scheme> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return schemeRepository.findByActiveTrue();
        }
        String lowerKeyword = keyword.toLowerCase();
        return schemeRepository.findByActiveTrue().stream()
                .filter(s -> (s.getName() != null && s.getName().toLowerCase().contains(lowerKeyword)) ||
                        (s.getShortTitle() != null && s.getShortTitle().toLowerCase().contains(lowerKeyword)) ||
                        (s.getDescription() != null && s.getDescription().toLowerCase().contains(lowerKeyword)))
                .collect(Collectors.toList());
    }

    /**
     * High-Performance Single-Pass Eligibility Filters & Relevance Ranking Logic
     */
    public List<Scheme> getEligibleSchemesForUser(User user) {
        List<Scheme> allSchemes = schemeRepository.findByActiveTrue();
        Map<String, Double> scoreCache = new HashMap<>();
        List<Scheme> eligibleSchemes = new ArrayList<>();

        Integer age = user.getAge();
        String gender = user.getGender();
        String occupation = user.getOccupation();
        String maritalStatus = user.getMaritalStatus();

        for (Scheme scheme : allSchemes) {
            if (passesHardLimits(scheme, age, gender)) {
                double score = calculateRelevanceScore(scheme, occupation, maritalStatus);
                scoreCache.put(scheme.getId(), score);
                eligibleSchemes.add(scheme);
            }
        }

        return eligibleSchemes.stream()
                .sorted((s1, s2) -> Double.compare(
                        scoreCache.getOrDefault(s2.getId(), 0.0),
                        scoreCache.getOrDefault(s1.getId(), 0.0)
                ))
                .collect(Collectors.toList());
    }

    private boolean passesHardLimits(Scheme scheme, Integer age, String gender) {
        if (age != null) {
            if (scheme.getMinAge() != null && age < scheme.getMinAge()) return false;
            if (scheme.getMaxAge() != null && age > scheme.getMaxAge()) return false;
        }

        if (scheme.getGender() != null && !scheme.getGender().isBlank() && gender != null) {
            return scheme.getGender().equalsIgnoreCase("Any") ||
                    scheme.getGender().equalsIgnoreCase(gender);
        }

        return true;
    }

    private double calculateRelevanceScore(Scheme scheme, String occupation, String maritalStatus) {
        double score = 0.0;

        if (scheme.getOccupation() != null && occupation != null
                && scheme.getOccupation().toLowerCase().contains(occupation.toLowerCase())) {
            score += 10.0;
        }

        if (scheme.getMaritalStatus() != null && maritalStatus != null
                && scheme.getMaritalStatus().equalsIgnoreCase(maritalStatus)) {
            score += 5.0;
        }

        return score;
    }

    /**
     * Helper pattern matching extractor to normalize dynamic currency values into floats
     */
    private double parseBenefitAmount(String benefitText) {
        if (benefitText == null || benefitText.isBlank()) {
            return 0.0;
        }
        try {
            String cleanText = benefitText.replaceAll("[^0-9.]", "");
            return cleanText.isEmpty() ? 0.0 : Double.parseDouble(cleanText);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    /**
     * Resolves error: cannot find symbol method newEligibleSchemesSince(User, LocalDateTime)
     * Finds schemes created after a specific timestamp that the user qualifies for.
     */
    public List<Scheme> newEligibleSchemesSince(User user, java.time.LocalDateTime lastLoginTime) {
        if (user == null || lastLoginTime == null) {
            return Collections.emptyList();
        }

        // 1. Filter active schemes created AFTER the provided timestamp
        List<Scheme> recentSchemes = schemeRepository.findByActiveTrue().stream()
                .filter(s -> s.getCreatedAt() != null && s.getCreatedAt().isAfter(lastLoginTime))
                .collect(Collectors.toList());

        // 2. Evaluate eligibility across these new items
        List<Scheme> newEligibleSchemes = new ArrayList<>();
        Integer age = user.getAge();
        String gender = user.getGender();

        for (Scheme scheme : recentSchemes) {
            if (passesHardLimits(scheme, age, gender)) {
                newEligibleSchemes.add(scheme);
            }
        }

        return newEligibleSchemes;
    }
}
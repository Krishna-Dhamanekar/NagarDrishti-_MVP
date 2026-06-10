package com.nagardrishti.service;

import com.nagardrishti.dto.EligibleSchemesResponse;
import com.nagardrishti.entity.Scheme;
import com.nagardrishti.entity.User;
import com.nagardrishti.repository.SchemeRepository;
import com.nagardrishti.repository.UserRepository; // Added this
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemeService {

    private final SchemeRepository schemeRepo;
    private final UserRepository   userRepo; // Swapped AuthService for UserRepository directly

    // ── Browse & Search ──────────────────────────────────────────────────────

    public List<Scheme> getAllSchemes() {
        return schemeRepo.findByActiveTrue();
    }

    public Scheme getSchemeById(String id) {
        return schemeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scheme not found: " + id));
    }

    public List<Scheme> getSchemesByCategory(String category) {
        return schemeRepo.findByActiveTrue().stream()
                .filter(s -> s.getSchemeCategory() != null && s.getSchemeCategory().stream().anyMatch(c -> c.equalsIgnoreCase(category)))
                .sorted(byPriority())
                .collect(Collectors.toList());
    }

    public List<Scheme> search(String q) {
        if (q == null || q.trim().isEmpty()) return schemeRepo.findByActiveTrue();
        return schemeRepo.searchActive(q.toLowerCase().trim());
    }

    // ── THE MATCH ENGINE ─────────────────────────────────────────────────────

    public EligibleSchemesResponse getEligibleSchemes(String userId) {
        // Fixes the getUserById problem safely via standard JPA findById
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        log.info("🔍 DEBUG: Fetched User -> State: {}, District: {}, Income: {}, Widow: {}, Girls: {}",
                user.getState(), user.getDistrict(), user.getAnnualIncome(), user.getWidow(), user.getGirlChildrenCount());

        List<Scheme> allSchemes = schemeRepo.findByActiveTrue();

        List<Scheme> topMatches = allSchemes.stream()
                .filter(s -> passesHardLimits(s, user))
                .filter(s -> calculateRelevanceScore(s, user) > 0)
                .sorted((s1, s2) -> {
                    int score1 = calculateRelevanceScore(s1, user);
                    int score2 = calculateRelevanceScore(s2, user);
                    if (score1 != score2) return Integer.compare(score2, score1);
                    return Double.compare(parseBenefitAmount(s2.getBenefitAmount()), parseBenefitAmount(s1.getBenefitAmount()));
                })
                .limit(40)
                .collect(Collectors.toList());

        double totalBenefit = topMatches.stream().mapToDouble(s -> parseBenefitAmount(s.getBenefitAmount())).sum();

        return EligibleSchemesResponse.builder()
                .totalEligibleSchemes(topMatches.size())
                .totalPotentialBenefit(totalBenefit)
                .schemes(topMatches)
                .build();
    }

    // ── Fixes 'Cannot resolve method newEligibleSchemesSince' ────────────────

    public List<Scheme> newEligibleSchemesSince(User user, LocalDateTime since) {
        if (since == null) return new ArrayList<>();
        return schemeRepo.findByActiveTrueAndCreatedAtAfter(since).stream()
                .filter(s -> passesHardLimits(s, user))
                .filter(s -> calculateRelevanceScore(s, user) > 0)
                .collect(Collectors.toList());
    }

    // ── SCORING ALGORITHM ────────────────────────────────────────────────────

    private int calculateRelevanceScore(Scheme s, User u) {
        int score = 0;

        if (s.getBeneficiaryState() != null && !s.getBeneficiaryState().isEmpty()) {
            boolean isExactState = s.getBeneficiaryState().stream().anyMatch(st -> st.equalsIgnoreCase(u.getState()));
            boolean isCentral = s.getBeneficiaryState().stream().anyMatch(st ->
                    st.equalsIgnoreCase("All") || st.equalsIgnoreCase("Central") || st.equalsIgnoreCase("Pan India") || st.equalsIgnoreCase("India"));

            if (isExactState) {
                score += 100;
                if (s.getBeneficiaryDistrict() != null && u.getDistrict() != null && s.getBeneficiaryDistrict().equalsIgnoreCase(u.getDistrict())) {
                    score += 50;
                }
            } else if (isCentral) {
                score += 10;
            } else {
                score -= 1000;
            }
        } else {
            score += 10;
        }

        if (Boolean.TRUE.equals(s.getTargetsWidows()) && Boolean.TRUE.equals(u.getWidow())) score += 150;
        if (Boolean.TRUE.equals(s.getTargetsGirlChild()) && u.getGirlChildrenCount() != null && u.getGirlChildrenCount() > 0) score += 120;
        if (Boolean.TRUE.equals(s.getTargetsSeniorCitizens()) && Boolean.TRUE.equals(u.getSeniorCitizenInFamily())) score += 80;

        if (Boolean.TRUE.equals(s.getRequiresDisability()) && Boolean.TRUE.equals(u.getDisabled())) score += 80;
        if (Boolean.TRUE.equals(s.getRequiresBpl()) && Boolean.TRUE.equals(u.getBpl())) score += 60;

        if (s.getEligibleCategories() != null && u.getCategory() != null && s.getEligibleCategories().contains(u.getCategory())) score += 50;

        if (s.getGender() != null && !s.getGender().equalsIgnoreCase("All") && !s.getGender().equalsIgnoreCase("Any")) {
            if (u.getGender() != null && s.getGender().equalsIgnoreCase(u.getGender())) score += 40;
        }

        if (s.getMinAge() != null || s.getMaxAge() != null) score += 20;

        if (s.getMaritalStatus() != null && u.getMaritalStatus() != null && s.getMaritalStatus().equalsIgnoreCase(u.getMaritalStatus())) score += 50;
        if (s.getOccupation() != null && u.getOccupation() != null && s.getOccupation().equalsIgnoreCase(u.getOccupation())) score += 60;
        if (s.getTargetEducationLevel() != null && u.getEducationLevel() != null && s.getTargetEducationLevel().equalsIgnoreCase(u.getEducationLevel())) score += 50;

        return score;
    }

    // ── HARD LIMITS (Non-Negotiable Rules) ───────────────────────────────────

    private boolean passesHardLimits(Scheme s, User u) {
        if (s.getMinAge() != null || s.getMaxAge() != null) {
            if (u.getAge() == null) return false;
            if (s.getMinAge() != null && u.getAge() < s.getMinAge()) return false;
            if (s.getMaxAge() != null && u.getAge() > s.getMaxAge()) return false;
        }

        if (s.getGender() != null && !s.getGender().equalsIgnoreCase("All") && !s.getGender().equalsIgnoreCase("Any")) {
            if (u.getGender() == null || !s.getGender().equalsIgnoreCase(u.getGender())) return false;
        }

        if (s.getMaxIncome() != null && s.getMaxIncome() > 0) {
            if (u.getAnnualIncome() == null || u.getAnnualIncome() > s.getMaxIncome()) return false;
        }

        if (s.getEligibleCategories() != null && !s.getEligibleCategories().isEmpty()) {
            if (!s.getEligibleCategories().contains("All") && (u.getCategory() == null || !s.getEligibleCategories().contains(u.getCategory()))) {
                return false;
            }
        }

        if (Boolean.TRUE.equals(s.getRequiresBpl()) && !Boolean.TRUE.equals(u.getBpl())) return false;
        if (Boolean.TRUE.equals(s.getRequiresDisability()) && !Boolean.TRUE.equals(u.getDisabled())) return false;

        if (s.getMaxLandAllowed() != null) {
            double userLand = (Boolean.TRUE.equals(u.getOwnsLand()) && u.getLandInAcres() != null) ? u.getLandInAcres() : 0.0;
            if (userLand > s.getMaxLandAllowed()) return false;
        }

        if (Boolean.TRUE.equals(s.getRequiresRationCard()) && !Boolean.TRUE.equals(u.getRationCard())) return false;
        if (Boolean.TRUE.equals(s.getRequiresBankAccount()) && !Boolean.TRUE.equals(u.getBankAccount())) return false;
        if (Boolean.TRUE.equals(s.getRequiresAadhaarLinkedBank()) && !Boolean.TRUE.equals(u.getAadhaarLinked())) return false;

        return true;
    }

    // ── Helper Utilities ─────────────────────────────────────────────────────

    private Comparator<Scheme> byPriority() {
        return Comparator.comparingInt(s -> s.getPriority() != null ? s.getPriority() : Integer.MAX_VALUE);
    }

    private double parseBenefitAmount(String raw) {
        if (raw == null || raw.isBlank()) return 0;
        String lower = raw.toLowerCase();
        double multiplier = 1;
        if (lower.contains("crore") || lower.contains(" cr")) multiplier = 10000000;
        else if (lower.contains("lakh") || lower.contains(" lac")) multiplier = 100000;
        if (lower.contains("/month") || lower.contains("per month") || lower.contains("monthly")) multiplier *= 12;

        String cleaned = raw.replaceAll("[^0-9.]", "");
        if (cleaned.isBlank() || cleaned.equals(".")) return 0;
        try { return Double.parseDouble(cleaned) * multiplier; } catch (NumberFormatException e) { return 0; }
    }
}
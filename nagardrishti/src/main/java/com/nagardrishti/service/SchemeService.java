package com.nagardrishti.service;

import com.nagardrishti.dto.EligibleSchemesResponse;
import com.nagardrishti.entity.Scheme;
import com.nagardrishti.entity.User;
import com.nagardrishti.repository.SchemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class SchemeService {

    private final SchemeRepository schemeRepo;
    private final AuthService      authService;

    // ── Browse ────────────────────────────────────────────────────────────────

    public List<Scheme> getAllSchemes() {
        return schemeRepo.findByActiveTrue();
    }

    public Scheme getSchemeById(String id) {
        return schemeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scheme not found: " + id));
    }

    public List<Scheme> getSchemesByCategory(String category) {
        return schemeRepo.findByActiveTrue().stream()
                .filter(s -> s.getSchemeCategory() != null &&
                        s.getSchemeCategory().stream().anyMatch(c -> c.equalsIgnoreCase(category)))
                .sorted(byPriority())
                .collect(Collectors.toList());
    }

    // ── Multi-word search ─────────────────────────────────────────────────────

    public List<Scheme> search(String query) {
        if (query == null || query.trim().length() < 3) return List.of();

        String   fullQ = query.trim().toLowerCase();
        String[] words = fullQ.split("\\s+");

        LinkedHashMap<String, Scheme> resultMap = new LinkedHashMap<>();
        for (String word : words) {
            if (word.length() >= 2) {
                schemeRepo.searchActive(word).forEach(s -> resultMap.putIfAbsent(s.getId(), s));
            }
        }

        schemeRepo.findByActiveTrue().stream()
                .filter(s -> !resultMap.containsKey(s.getId()))
                .filter(s -> anyWordMatches(words, s))
                .forEach(s -> resultMap.putIfAbsent(s.getId(), s));

        return resultMap.values().stream()
                .sorted(Comparator
                        .comparingInt((Scheme s) -> countWordMatches(words, s)).reversed()
                        .thenComparingInt(s -> s.getPriority() != null ? s.getPriority() : Integer.MAX_VALUE))
                .limit(60)
                .collect(Collectors.toList());
    }

    private boolean anyWordMatches(String[] words, Scheme s) {
        for (String w : words) {
            if (listContains(s.getTags(), w)) return true;
            if (listContains(s.getSchemeCategory(), w)) return true;
            if (listContains(s.getBeneficiaryState(), w)) return true;
            if (s.getSchemeFor() != null && s.getSchemeFor().toLowerCase().contains(w)) return true;
        }
        return false;
    }

    private int countWordMatches(String[] words, Scheme s) {
        int count = 0;
        String hay = ((s.getName()        == null ? "" : s.getName()) + " " +
                (s.getShortTitle()  == null ? "" : s.getShortTitle()) + " " +
                (s.getDescription() == null ? "" : s.getDescription())).toLowerCase();
        for (String w : words) {
            if (hay.contains(w) || listContains(s.getTags(), w) ||
                    listContains(s.getSchemeCategory(), w)) count++;
        }
        return count;
    }

    private boolean listContains(List<String> list, String word) {
        return list != null && list.stream().anyMatch(v -> v.toLowerCase().contains(word));
    }

    // ── Eligibility — positive-match scoring ──────────────────────────────────

    public EligibleSchemesResponse getEligibleSchemes(String userId) {
        User user = authService.getUserById(userId);

        List<Scheme> eligible = schemeRepo.findByActiveTrue().stream()
                .filter(s -> matchAge(s, user))
                .filter(s -> matchGender(s, user))
                .filter(s -> matchIncome(s, user))
                .filter(s -> matchBpl(s, user))
                .filter(s -> matchDisability(s, user))
                .filter(s -> matchCategory(s, user))
                .filter(s -> matchOccupation(s, user))
                .filter(s -> matchLand(s, user))
                .filter(s -> matchEducation(s, user))
                .filter(s -> matchState(s, user))
                .filter(s -> positiveMatchScore(s, user) >= 1)
                .sorted(Comparator
                        .comparingInt((Scheme s) -> positiveMatchScore(s, user)).reversed()
                        .thenComparingInt(s -> s.getPriority() != null ? s.getPriority() : Integer.MAX_VALUE))
                .collect(Collectors.toList());

        double totalBenefit = eligible.stream()
                .mapToDouble(s -> parseBenefitAmount(s.getBenefitAmount()))
                .sum();

        log.info("Eligible schemes for user {} ({}): {}", user.getFullName(), userId, eligible.size());

        return EligibleSchemesResponse.builder()
                .totalEligibleSchemes(eligible.size())
                .totalPotentialBenefit(totalBenefit)
                .schemes(eligible)
                .build();
    }

    // ── New scheme notifications ──────────────────────────────────────────────

    public List<Scheme> newEligibleSchemesSince(User user, LocalDateTime since) {
        if (since == null) return List.of();
        return schemeRepo.findByActiveTrueAndCreatedAtAfter(since).stream()
                .filter(s -> matchAge(s, user))
                .filter(s -> matchGender(s, user))
                .filter(s -> matchIncome(s, user))
                .filter(s -> matchBpl(s, user))
                .filter(s -> matchDisability(s, user))
                .filter(s -> matchCategory(s, user))
                .filter(s -> matchOccupation(s, user))
                .filter(s -> matchLand(s, user))
                .filter(s -> matchEducation(s, user))
                .filter(s -> matchState(s, user))
                .filter(s -> positiveMatchScore(s, user) >= 1)
                .sorted(byPriority())
                .limit(20)
                .collect(Collectors.toList());
    }

    // ── Positive match score ──────────────────────────────────────────────────

    private int positiveMatchScore(Scheme s, User u) {
        int score = 0;

        if ("State".equalsIgnoreCase(s.getLevel())
                && u.getState() != null
                && s.getBeneficiaryState() != null
                && s.getBeneficiaryState().stream().anyMatch(st -> st.equalsIgnoreCase(u.getState())))
            score += 2;

        if (s.getEligibleCategories() != null && !s.getEligibleCategories().isEmpty()
                && u.getCategory() != null
                && s.getEligibleCategories().stream().anyMatch(c -> c.equalsIgnoreCase(u.getCategory())))
            score += 2;

        if (s.getGender() != null && !"All".equalsIgnoreCase(s.getGender())
                && u.getGender() != null && s.getGender().equalsIgnoreCase(u.getGender()))
            score += 1;

        if (s.getMaxIncome() != null && u.getAnnualIncome() != null
                && u.getAnnualIncome() <= s.getMaxIncome())
            score += 1;

        if (Boolean.TRUE.equals(s.getRequiresBpl()) && Boolean.TRUE.equals(u.getBpl()))
            score += 2;

        if (Boolean.TRUE.equals(s.getRequiresDisability()) && Boolean.TRUE.equals(u.getDisabled()))
            score += 2;

        if (s.getOccupation() != null && !s.getOccupation().isBlank()
                && u.getOccupation() != null
                && s.getOccupation().equalsIgnoreCase(u.getOccupation()))
            score += 2;

        if ((s.getMinAge() != null || s.getMaxAge() != null) && u.getAge() != null)
            score += 1;

        if (s.getTargetEducationLevel() != null && !s.getTargetEducationLevel().isBlank()
                && s.getTargetEducationLevel().equalsIgnoreCase(u.getEducationLevel()))
            score += 1;

        if (Boolean.TRUE.equals(u.getWidow())
                && s.getTags() != null
                && s.getTags().stream().anyMatch(t -> t.toLowerCase().contains("widow")))
            score += 2;

        return score;
    }

    // ── Hard match predicates ─────────────────────────────────────────────────

    private boolean matchAge(Scheme s, User u) {
        if (u.getAge() == null) return true;
        if (s.getMinAge() != null && u.getAge() < s.getMinAge()) return false;
        if (s.getMaxAge() != null && u.getAge() > s.getMaxAge()) return false;
        return true;
    }

    private boolean matchGender(Scheme s, User u) {
        if (s.getGender() == null || "All".equalsIgnoreCase(s.getGender())) return true;
        return u.getGender() != null && s.getGender().equalsIgnoreCase(u.getGender());
    }

    private boolean matchIncome(Scheme s, User u) {
        if (s.getMaxIncome() == null) return true;
        if (u.getAnnualIncome() == null) return false;
        return u.getAnnualIncome() <= s.getMaxIncome();
    }

    private boolean matchBpl(Scheme s, User u) {
        if (!Boolean.TRUE.equals(s.getRequiresBpl())) return true;
        return Boolean.TRUE.equals(u.getBpl());
    }

    private boolean matchDisability(Scheme s, User u) {
        if (!Boolean.TRUE.equals(s.getRequiresDisability())) return true;
        return Boolean.TRUE.equals(u.getDisabled());
    }

    private boolean matchCategory(Scheme s, User u) {
        if (s.getEligibleCategories() == null || s.getEligibleCategories().isEmpty()) return true;
        if (u.getCategory() == null) return false;
        return s.getEligibleCategories().stream().anyMatch(c -> c.equalsIgnoreCase(u.getCategory()));
    }

    private boolean matchOccupation(Scheme s, User u) {
        if (s.getOccupation() == null || s.getOccupation().isBlank()) return true;
        return u.getOccupation() != null && s.getOccupation().equalsIgnoreCase(u.getOccupation());
    }

    private boolean matchLand(Scheme s, User u) {
        if (s.getMaxLandAllowed() == null) return true;
        if (u.getLandInAcres() == null) return false;
        return u.getLandInAcres() <= s.getMaxLandAllowed();
    }

    private boolean matchEducation(Scheme s, User u) {
        if (s.getTargetEducationLevel() == null || s.getTargetEducationLevel().isBlank()) return true;
        return s.getTargetEducationLevel().equalsIgnoreCase(u.getEducationLevel());
    }

    private boolean matchState(Scheme s, User u) {
        if ("Central".equalsIgnoreCase(s.getLevel())) return true;
        if ("State".equalsIgnoreCase(s.getLevel())) {
            if (u.getState() == null) return false;
            if (s.getBeneficiaryState() == null || s.getBeneficiaryState().isEmpty()) return false;
            return s.getBeneficiaryState().stream().anyMatch(st -> st.equalsIgnoreCase(u.getState()));
        }
        return false;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Comparator<Scheme> byPriority() {
        return Comparator.comparingInt(s ->
                s.getPriority() != null ? s.getPriority() : Integer.MAX_VALUE);
    }

    private double parseBenefitAmount(String raw) {
        if (raw == null || raw.isBlank()) return 0;
        String cleaned = raw.replaceAll("[\\u20b9,]", "").trim();
        java.util.regex.Matcher m =
                java.util.regex.Pattern.compile("([0-9]+(?:\\.[0-9]+)?)").matcher(cleaned);
        if (!m.find()) return 0;
        double val = Double.parseDouble(m.group(1));
        String lower = cleaned.toLowerCase();
        if (lower.contains("lakh"))                            val *= 100_000;
        else if (lower.contains("crore") || lower.contains(" cr")) val *= 10_000_000;
        return val;
    }
}
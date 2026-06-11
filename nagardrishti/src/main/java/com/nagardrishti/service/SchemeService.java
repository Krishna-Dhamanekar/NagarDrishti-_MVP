package com.nagardrishti.service;

import com.nagardrishti.dto.EligibleSchemesResponse;
import com.nagardrishti.entity.Scheme;
import com.nagardrishti.entity.User;
import com.nagardrishti.repository.SchemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable; // <-- ADDED THIS IMPORT
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
        String   q     = query.trim().toLowerCase();
        String[] words = q.split("\\s+");

        LinkedHashMap<String, Scheme> resultMap = new LinkedHashMap<>();
        for (String word : words) {
            if (word.length() >= 2)
                schemeRepo.searchActive(word).forEach(s -> resultMap.putIfAbsent(s.getId(), s));
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
        String hay = (nvl(s.getName()) + " " + nvl(s.getShortTitle()) + " " + nvl(s.getDescription())).toLowerCase();
        for (String w : words) {
            if (hay.contains(w) || listContains(s.getTags(), w) || listContains(s.getSchemeCategory(), w)) count++;
        }
        return count;
    }

    private boolean listContains(List<String> list, String word) {
        return list != null && list.stream().anyMatch(v -> v.toLowerCase().contains(word));
    }

    private String nvl(String s) { return s == null ? "" : s; }

    // ── Eligibility — TAG + CATEGORY based matching ───────────────────────────

    @Cacheable(value = "eligibleSchemes", key = "#userId") // <-- ADDED CACHING ANNOTATION HERE
    public EligibleSchemesResponse getEligibleSchemes(String userId) {
        User user = authService.getUserById(userId);

        List<Scheme> eligible = schemeRepo.findByActiveTrue().stream()
                .filter(s -> passHardGates(s, user))
                .filter(s -> eligibilityScore(s, user) >= 1)
                .sorted(Comparator
                        // 1. Sort by Highest Relevance Score first
                        .comparingInt((Scheme s) -> eligibilityScore(s, user)).reversed()
                        // 2. Put targeted schemes above universal schemes (universal = lower priority)
                        .thenComparing(this::isUniversalScheme)
                        // 3. Fall back to admin priority
                        .thenComparingInt(s -> s.getPriority() != null ? s.getPriority() : Integer.MAX_VALUE))
                .limit(50) // <-- CAP AT 50 SCHEMES MAXIMUM
                .collect(Collectors.toList());

        log.info("Eligible schemes for user {} ({}): {} (Capped at 50, Caching Active)", user.getFullName(), userId, eligible.size());

        return EligibleSchemesResponse.builder()
                .totalEligibleSchemes(eligible.size())
                .totalPotentialBenefit(0.0) // Benefit breakdown removed from frontend
                .schemes(eligible)
                .build();
    }

    public List<Scheme> newEligibleSchemesSince(User user, LocalDateTime since) {
        if (since == null) return List.of();
        return schemeRepo.findByActiveTrueAndCreatedAtAfter(since).stream()
                .filter(s -> passHardGates(s, user))
                .filter(s -> eligibilityScore(s, user) >= 1)
                .sorted(byPriority())
                .limit(20)
                .collect(Collectors.toList());
    }

    // ── Hard gates — disqualifying checks ────────────────────────────────────

    private boolean passHardGates(Scheme s, User u) {
        // Age
        if (u.getAge() != null) {
            if (s.getMinAge() != null && u.getAge() < s.getMinAge()) return false;
            if (s.getMaxAge() != null && u.getAge() > s.getMaxAge()) return false;
        }
        // Gender
        if (s.getGender() != null && !"All".equalsIgnoreCase(s.getGender())) {
            if (u.getGender() != null && !s.getGender().equalsIgnoreCase(u.getGender())) return false;
        }
        // Income
        if (s.getMaxIncome() != null && u.getAnnualIncome() != null) {
            if (u.getAnnualIncome() > s.getMaxIncome()) return false;
        }
        // BPL required
        if (Boolean.TRUE.equals(s.getRequiresBpl()) && !Boolean.TRUE.equals(u.getBpl())) return false;
        // Disability required
        if (Boolean.TRUE.equals(s.getRequiresDisability()) && !Boolean.TRUE.equals(u.getDisabled())) return false;
        // Occupation required
        if (s.getOccupation() != null && !s.getOccupation().isBlank()) {
            if (u.getOccupation() == null || !s.getOccupation().equalsIgnoreCase(u.getOccupation())) return false;
        }
        // Education required
        if (s.getTargetEducationLevel() != null && !s.getTargetEducationLevel().isBlank()) {
            if (u.getEducationLevel() == null || !s.getTargetEducationLevel().equalsIgnoreCase(u.getEducationLevel())) return false;
        }
        // eligibleCategories
        if (s.getEligibleCategories() != null && !s.getEligibleCategories().isEmpty()) {
            if (u.getCategory() == null) return false;
            if (s.getEligibleCategories().stream().noneMatch(c -> c.equalsIgnoreCase(u.getCategory()))) return false;
        }
        // State
        if ("State".equalsIgnoreCase(s.getLevel())) {
            if (s.getBeneficiaryState() == null || s.getBeneficiaryState().isEmpty()) return true;
            if (u.getState() == null) return false;
            if (s.getBeneficiaryState().stream().noneMatch(st -> st.equalsIgnoreCase(u.getState()))) return false;
        }
        return true;
    }

    // ── Eligibility score — tag + category based ──────────────────────────────

    private int eligibilityScore(Scheme s, User u) {
        int score = 0;
        String schemeText = buildSchemeText(s);
        Set<String> userKeywords = buildUserKeywords(u);

        for (String kw : userKeywords) {
            if (schemeText.contains(kw)) {
                score += 2;
                break;
            }
        }

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

        if ((s.getMinAge() != null || s.getMaxAge() != null) && u.getAge() != null)
            score += 1;

        if (s.getTargetEducationLevel() != null && !s.getTargetEducationLevel().isBlank()
                && s.getTargetEducationLevel().equalsIgnoreCase(u.getEducationLevel()))
            score += 1;

        return score;
    }

    // ── Helper to determine if a scheme is broad/universal ────────────────────

    private boolean isUniversalScheme(Scheme s) {
        return (s.getMinAge() == null && s.getMaxAge() == null) &&
                (s.getGender() == null || "All".equalsIgnoreCase(s.getGender())) &&
                (s.getMaxIncome() == null) &&
                !Boolean.TRUE.equals(s.getRequiresBpl()) &&
                !Boolean.TRUE.equals(s.getRequiresDisability()) &&
                (s.getOccupation() == null || s.getOccupation().isBlank()) &&
                (s.getTargetEducationLevel() == null || s.getTargetEducationLevel().isBlank()) &&
                (s.getEligibleCategories() == null || s.getEligibleCategories().isEmpty());
    }

    // ── Build scheme searchable text ──────────────────────────────────────────

    private String buildSchemeText(Scheme s) {
        StringBuilder sb = new StringBuilder();
        if (s.getTags()           != null) sb.append(String.join(" ", s.getTags())).append(" ");
        if (s.getSchemeCategory() != null) sb.append(String.join(" ", s.getSchemeCategory())).append(" ");
        if (s.getName()           != null) sb.append(s.getName()).append(" ");
        if (s.getDescription()    != null) sb.append(s.getDescription().substring(0, Math.min(200, s.getDescription().length()))).append(" ");
        if (s.getSchemeFor()      != null) sb.append(s.getSchemeFor()).append(" ");
        return sb.toString().toLowerCase();
    }

    // ── Build user keyword set from profile ───────────────────────────────────

    private Set<String> buildUserKeywords(User u) {
        Set<String> kw = new LinkedHashSet<>();

        if (u.getCategory() != null) {
            switch (u.getCategory().toUpperCase()) {
                case "SC" -> kw.addAll(List.of("sc", "scheduled caste", "dalit", "sc/st", "scst", "harijan"));
                case "ST" -> kw.addAll(List.of("st", "scheduled tribe", "tribal", "adivasi", "sc/st", "scst", "vanvasi"));
                case "OBC" -> kw.addAll(List.of("obc", "other backward", "backward class", "ebc", "sebc", "obc/ebc"));
                case "MINORITY" -> kw.addAll(List.of("minority", "minorities", "muslim", "christian", "sikh", "buddhist", "parsi", "jain"));
                case "GEN" -> kw.addAll(List.of("general", "unreserved", "open category"));
            }
        }

        if ("FEMALE".equalsIgnoreCase(u.getGender())) {
            kw.addAll(List.of("women", "woman", "girl", "female", "mahila", "stree", "beti",
                    "mother", "pregnant", "maternity", "widows", "lady", "ladies"));
        }
        if ("MALE".equalsIgnoreCase(u.getGender())) {
            kw.addAll(List.of("male", "men", "man", "boy", "father"));
        }

        if (Boolean.TRUE.equals(u.getBpl())) {
            kw.addAll(List.of("bpl", "below poverty", "poor", "destitute", "economically weaker", "ews"));
        }

        if (Boolean.TRUE.equals(u.getDisabled())) {
            kw.addAll(List.of("disability", "disabled", "differently abled", "pwd",
                    "handicap", "divyang", "specially abled", "physically challenged"));
        }

        if (Boolean.TRUE.equals(u.getWidow())) {
            kw.addAll(List.of("widow", "widower", "single woman", "destitute woman"));
        }

        if (Boolean.TRUE.equals(u.getSeniorCitizenInFamily())) {
            kw.addAll(List.of("senior citizen", "old age", "elderly", "pension", "aged person", "senior"));
        }

        if (u.getGirlChildrenCount() != null && u.getGirlChildrenCount() > 0) {
            kw.addAll(List.of("girl child", "beti bachao", "daughter", "sukanya"));
        }

        if (u.getOccupation() != null && !u.getOccupation().isBlank()) {
            String occ = u.getOccupation().toLowerCase().trim();
            kw.add(occ);
            if (occ.contains("farm") || occ.equals("farmer"))
                kw.addAll(List.of("farmer", "farming", "agriculture", "kisan", "krishi",
                        "agricultural", "agri", "cultivator", "crop"));
            if (occ.contains("student"))
                kw.addAll(List.of("student", "scholarship", "education", "study", "school", "college"));
            if (occ.contains("fish"))
                kw.addAll(List.of("fisherman", "fishing", "fisheries", "fisher", "aquaculture"));
            if (occ.contains("labour") || occ.contains("labor"))
                kw.addAll(List.of("labour", "labor", "worker", "unorganised", "unorganized",
                        "daily wage", "construction worker", "migrant"));
            if (occ.contains("weaver") || occ.contains("handloom"))
                kw.addAll(List.of("weaver", "handloom", "textile", "craft", "artisan"));
            if (occ.contains("shepherd") || occ.contains("animal"))
                kw.addAll(List.of("animal husbandry", "livestock", "shepherd", "dairy", "cattle"));
            if (occ.contains("self employ") || occ.contains("entrepreneur") || occ.contains("business"))
                kw.addAll(List.of("self employment", "entrepreneur", "startup", "msme", "business", "enterprise"));
        }

        if (u.getEducationLevel() != null) {
            switch (u.getEducationLevel().toUpperCase()) {
                case "ITI"          -> kw.addAll(List.of("iti", "industrial training", "vocational"));
                case "DIPLOMA"      -> kw.addAll(List.of("diploma", "polytechnic"));
                case "GRADUATE"     -> kw.addAll(List.of("graduate", "degree", "graduation", "bachelor"));
                case "POSTGRADUATE" -> kw.addAll(List.of("postgraduate", "masters", "pg", "post graduation"));
                case "DOCTORATE"    -> kw.addAll(List.of("phd", "doctorate", "research"));
                case "HSC"          -> kw.addAll(List.of("12th", "hsc", "higher secondary", "puc", "class 12"));
                case "SECONDARY"    -> kw.addAll(List.of("10th", "secondary", "matriculation", "sslc", "class 10"));
            }
        }

        if (Boolean.TRUE.equals(u.getOwnsLand())) {
            kw.addAll(List.of("farmer", "agriculture", "kisan", "land", "cultivation", "irrigation"));
        }

        return kw;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Comparator<Scheme> byPriority() {
        return Comparator.comparingInt(s ->
                s.getPriority() != null ? s.getPriority() : Integer.MAX_VALUE);
    }

    private double parseBenefitAmount(String raw) {
        if (raw == null || raw.isBlank()) return 0;
        String lower = raw.toLowerCase();
        boolean isLakh  = lower.contains("lakh") || lower.contains(" lac");
        boolean isCrore = lower.contains("crore") || lower.contains(" cr");

        String cleaned = raw
                .replace("\u20b9", "")
                .replaceAll("(?i)rs\\.?\\s*", "")
                .replaceAll("(?i)inr\\s*", "")
                .replaceAll(",", "")
                .trim();

        java.util.regex.Matcher m =
                java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)").matcher(cleaned);
        if (!m.find()) return 0;

        double val = Double.parseDouble(m.group(1));
        if      (isCrore) val *= 10_000_000;
        else if (isLakh)  val *= 100_000;
        return val;
    }
}
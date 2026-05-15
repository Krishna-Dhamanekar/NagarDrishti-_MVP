package com.nagardrishti.service;

import com.nagardrishti.dto.EligibleSchemesResponse;
import com.nagardrishti.entity.*;
import com.nagardrishti.repository.SchemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class SchemeService {

    private final SchemeRepository schemeRepo;
    private final AuthService authService;

    public List<Scheme> getAllSchemes() { return schemeRepo.findByIsActiveTrue(); }

    public Scheme getSchemeById(String id) {
        return schemeRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Scheme not found: " + id));
    }

    public List<Scheme> getSchemesByCategory(String category) {
        return schemeRepo.findByCategoryIgnoreCaseAndIsActiveTrue(category);
    }

    public EligibleSchemesResponse getEligibleSchemes(String userId) {
        User user = authService.getUserById(userId);
        List<Scheme> eligible = schemeRepo.findByIsActiveTrue().stream()
            .filter(s -> matchAge(s, user))
            .filter(s -> matchGender(s, user))
            .filter(s -> matchIncome(s, user))
            .filter(s -> matchCategory(s, user))
            .filter(s -> matchMarital(s, user))
            .filter(s -> matchState(s, user))
            .collect(Collectors.toList());

        double totalBenefit = eligible.stream()
            .mapToDouble(s -> s.getBenefitAmount() != null ? s.getBenefitAmount() : 0).sum();

        return EligibleSchemesResponse.builder()
            .totalEligibleSchemes(eligible.size())
            .totalPotentialBenefit(totalBenefit)
            .schemes(eligible).build();
    }

    private boolean matchAge(Scheme s, User u) {
        if (u.getAge() == null) return true;
        if (s.getMinAge() != null && u.getAge() < s.getMinAge()) return false;
        if (s.getMaxAge() != null && u.getAge() > s.getMaxAge()) return false;
        return true;
    }
    private boolean matchGender(Scheme s, User u) {
        if (s.getGender() == null || "ALL".equalsIgnoreCase(s.getGender())) return true;
        return u.getGender() == null || s.getGender().equalsIgnoreCase(u.getGender());
    }
    private boolean matchIncome(Scheme s, User u) {
        if (s.getMaxAnnualIncome() == null || u.getAnnualIncome() == null) return true;
        return u.getAnnualIncome() <= s.getMaxAnnualIncome();
    }
    private boolean matchCategory(Scheme s, User u) {
        if (s.getEligibleCategories() == null || s.getEligibleCategories().isEmpty()) return true;
        if (u.getCategory() == null) return true;
        return s.getEligibleCategories().stream().anyMatch(c -> c.equalsIgnoreCase(u.getCategory()));
    }
    private boolean matchMarital(Scheme s, User u) {
        if (s.getMaritalStatus() == null || "ANY".equalsIgnoreCase(s.getMaritalStatus())) return true;
        return u.getMaritalStatus() == null || s.getMaritalStatus().equalsIgnoreCase(u.getMaritalStatus());
    }
    private boolean matchState(Scheme s, User u) {
        if ("CENTRAL".equalsIgnoreCase(s.getScope())) return true;
        if ("STATE".equalsIgnoreCase(s.getScope())) {
            if (u.getState() == null || s.getApplicableState() == null) return false;
            return s.getApplicableState().equalsIgnoreCase(u.getState());
        }
        return true;
    }
}

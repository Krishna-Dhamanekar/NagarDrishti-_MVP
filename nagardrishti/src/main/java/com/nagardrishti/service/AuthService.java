package com.nagardrishti.service;

import com.nagardrishti.dto.*;
import com.nagardrishti.entity.Scheme;
import com.nagardrishti.entity.User;
import com.nagardrishti.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j @Service
public class AuthService {

    private final UserRepository  userRepo;
    private final PasswordEncoder passwordEncoder;
    private final SchemeService   schemeService;

    // Manual constructor — @Lazy on SchemeService breaks the circular dependency.
    // AuthService → SchemeService → AuthService would fail at startup without @Lazy.
    @Autowired
    public AuthService(UserRepository userRepo,
                       PasswordEncoder passwordEncoder,
                       @Lazy SchemeService schemeService) {
        this.userRepo        = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.schemeService   = schemeService;
    }

    // ── Register ──────────────────────────────────────────────────────────────
    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByPhoneNumber(req.getPhoneNumber()))
            throw new IllegalArgumentException("Phone number already registered. Please login instead.");
        if (req.getEmail() != null && !req.getEmail().isBlank()
                && userRepo.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already registered.");

        User user = User.builder()
                .fullName(req.getFullName().trim())
                .phoneNumber(req.getPhoneNumber())
                .email(req.getEmail() != null ? req.getEmail().trim() : null)
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .age(req.getAge())
                .gender(req.getGender() != null ? req.getGender().toUpperCase() : null)
                .state(req.getState())
                .district(req.getDistrict())
                .pincode(req.getPincode())
                .category(req.getCategory() != null ? req.getCategory().toUpperCase() : null)
                .annualIncome(req.getAnnualIncome())
                .bpl(Boolean.TRUE.equals(req.getBpl()))
                .educationLevel(req.getEducationLevel())
                .occupation(req.getOccupation())
                .disabled(Boolean.TRUE.equals(req.getDisabled()))
                .disabilityPercentage(req.getDisabilityPercentage())
                .ownsLand(Boolean.TRUE.equals(req.getOwnsLand()))
                .landInAcres(req.getLandInAcres())
                .maritalStatus(req.getMaritalStatus() != null ? req.getMaritalStatus().toUpperCase() : null)
                .familyMembers(req.getFamilyMembers())
                .widow(Boolean.TRUE.equals(req.getWidow()))
                .seniorCitizenInFamily(Boolean.TRUE.equals(req.getSeniorCitizenInFamily()))
                .girlChildrenCount(req.getGirlChildrenCount() != null ? req.getGirlChildrenCount() : 0)
                .aadhaarLinked(Boolean.TRUE.equals(req.getAadhaarLinked()))
                .bankAccount(Boolean.TRUE.equals(req.getBankAccount()))
                .rationCard(Boolean.TRUE.equals(req.getRationCard()))
                .healthInsurance(Boolean.TRUE.equals(req.getHealthInsurance()))
                .lastLoginAt(LocalDateTime.now())
                .role("USER")
                .build();

        User saved = userRepo.save(user);
        log.info("New user registered: {} ({})", saved.getFullName(), saved.getId());
        return toResponse(saved, "Registration successful", List.of());
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByPhoneNumber(req.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("No account found with this phone number."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new IllegalArgumentException("Incorrect password.");

        // Capture previous login BEFORE updating
        LocalDateTime previousLogin = user.getLastLoginAt();

        // Update last login timestamp
        user.setLastLoginAt(LocalDateTime.now());
        userRepo.save(user);

        // Find new eligible schemes since last login
        List<Scheme> newSchemes = schemeService.newEligibleSchemesSince(user, previousLogin);

        log.info("User logged in: {} ({}) — {} new eligible schemes since last login",
                user.getFullName(), user.getId(), newSchemes.size());

        return toResponse(user, "Login successful", newSchemes);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    public User getUserById(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private AuthResponse toResponse(User u, String msg, List<Scheme> newSchemes) {
        List<String> names = newSchemes.stream()
                .limit(5)
                .map(s -> s.getShortTitle() != null ? s.getShortTitle() : s.getName())
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .userId(u.getId()).role(u.getRole()).message(msg)
                .fullName(u.getFullName()).phoneNumber(u.getPhoneNumber()).email(u.getEmail())
                .age(u.getAge()).gender(u.getGender())
                .state(u.getState()).district(u.getDistrict()).pincode(u.getPincode())
                .category(u.getCategory()).annualIncome(u.getAnnualIncome()).bpl(u.getBpl())
                .educationLevel(u.getEducationLevel()).occupation(u.getOccupation())
                .disabled(u.getDisabled()).disabilityPercentage(u.getDisabilityPercentage())
                .ownsLand(u.getOwnsLand()).landInAcres(u.getLandInAcres())
                .maritalStatus(u.getMaritalStatus()).familyMembers(u.getFamilyMembers())
                .widow(u.getWidow()).seniorCitizenInFamily(u.getSeniorCitizenInFamily())
                .girlChildrenCount(u.getGirlChildrenCount())
                .aadhaarLinked(u.getAadhaarLinked()).bankAccount(u.getBankAccount())
                .rationCard(u.getRationCard()).healthInsurance(u.getHealthInsurance())
                .newSchemesCount(newSchemes.size())
                .newSchemeNames(names)
                .build();
    }
}
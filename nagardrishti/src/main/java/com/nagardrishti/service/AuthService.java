package com.nagardrishti.service;

import com.nagardrishti.dto.*;
import com.nagardrishti.entity.Project;
import com.nagardrishti.entity.Scheme;
import com.nagardrishti.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthService {

    private final UserRepository    userRepo;
    private final PasswordEncoder   passwordEncoder;
    private final SchemeService     schemeService;
    private final ProjectService    projectService;

    @Autowired
    public AuthService(UserRepository userRepo,
                       PasswordEncoder passwordEncoder,
                       @Lazy SchemeService schemeService,
                       @Lazy ProjectService projectService) {
        this.userRepo        = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.schemeService   = schemeService;
        this.projectService  = projectService;
    }

    // ── Register ──────────────────────────────────────────────────────────────
    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already registered.");
        }

        User u = User.builder()
                .fullName(req.getFullName())
                .phoneNumber(req.getPhoneNumber())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .age(req.getAge())
                .gender(req.getGender())
                .state(req.getState())
                .district(req.getDistrict())
                .pincode(req.getPincode())
                .ward(req.getWard())
                .zone(req.getZone())
                .category(req.getCategory())
                .annualIncome(req.getAnnualIncome())
                .bpl(req.getBpl())
                .educationLevel(req.getEducationLevel())
                .occupation(req.getOccupation())
                .disabled(req.getDisabled())
                .disabilityPercentage(req.getDisabilityPercentage())
                .ownsLand(req.getOwnsLand())
                .landInAcres(req.getLandInAcres())
                .maritalStatus(req.getMaritalStatus())
                .familyMembers(req.getFamilyMembers())
                .widow(req.getWidow())
                .seniorCitizenInFamily(req.getSeniorCitizenInFamily())
                .girlChildrenCount(req.getGirlChildrenCount())
                .aadhaarLinked(req.getAadhaarLinked())
                .bankAccount(req.getBankAccount())
                .rationCard(req.getRationCard())
                .healthInsurance(req.getHealthInsurance())
                .role("USER")
                .lastLoginAt(LocalDateTime.now()) // Set initial login time so they don't get flooded with past notifications
                .build();

        u = userRepo.save(u);

        // No new notifications on first ever registration
        return buildAuthResponse(u, "Registration successful!", new ArrayList<>(), new ArrayList<>());
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest req) {
        User u = userRepo.findByPhoneNumber(req.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("Invalid phone number or password."));

        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid phone number or password.");
        }

        LocalDateTime lastLogin = u.getLastLoginAt();

        // ── Fetch Delta Notifications ──
        List<Scheme> newSchemes = new ArrayList<>();
        List<Project> newProjects = new ArrayList<>();

        if (lastLogin != null) {
            // Check for new schemes added since last login that THIS user is eligible for
            newSchemes = schemeService.newEligibleSchemesSince(u, lastLogin);

            // Check for new projects added in their area
            newProjects = projectService.getNewInAreaSince(lastLogin, u.getPincode(), u.getZone());
        }

        // Update login timestamp for the next time they log in
        u.setLastLoginAt(LocalDateTime.now());
        u = userRepo.save(u);

        return buildAuthResponse(u, "Login successful!", newSchemes, newProjects);
    }

    // ── User Lookup ───────────────────────────────────────────────────────────
    public User getUserById(String id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private AuthResponse buildAuthResponse(User u, String msg, List<Scheme> newSchemes, List<Project> newProjects) {

        // Truncate names if they are too long for the UI dropdown
        List<String> schemeNames = newSchemes.stream()
                .map(s -> s.getName() != null && s.getName().length() > 60
                        ? s.getName().substring(0, 57) + "..." : s.getName())
                .collect(Collectors.toList());

        List<String> projectNames = newProjects.stream()
                .map(p -> p.getName() != null && p.getName().length() > 60
                        ? p.getName().substring(0, 57) + "..." : p.getName())
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .userId(u.getId()).role(u.getRole()).message(msg)
                .fullName(u.getFullName()).phoneNumber(u.getPhoneNumber()).email(u.getEmail())
                .age(u.getAge()).gender(u.getGender())
                .state(u.getState()).district(u.getDistrict())
                .pincode(u.getPincode()).ward(u.getWard()).zone(u.getZone())
                .category(u.getCategory()).annualIncome(u.getAnnualIncome()).bpl(u.getBpl())
                .educationLevel(u.getEducationLevel()).occupation(u.getOccupation())
                .disabled(u.getDisabled()).disabilityPercentage(u.getDisabilityPercentage())
                .ownsLand(u.getOwnsLand()).landInAcres(u.getLandInAcres())
                .maritalStatus(u.getMaritalStatus()).familyMembers(u.getFamilyMembers())
                .widow(u.getWidow()).seniorCitizenInFamily(u.getSeniorCitizenInFamily())
                .girlChildrenCount(u.getGirlChildrenCount())
                .aadhaarLinked(u.getAadhaarLinked()).bankAccount(u.getBankAccount())
                .rationCard(u.getRationCard()).healthInsurance(u.getHealthInsurance())

                // Notifications payloads mapped to AuthResponse DTO
                .newSchemesCount(newSchemes.size())
                .newSchemeNames(schemeNames)
                .newProjectsCount(newProjects.size())
                .newProjectNames(projectNames)
                .build();
    }
}
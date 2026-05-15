package com.nagardrishti.service;

import com.nagardrishti.dto.*;
import com.nagardrishti.entity.User;
import com.nagardrishti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j @Service @RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByPhoneNumber(req.getPhoneNumber()))
            throw new IllegalArgumentException("Phone number already registered. Please login instead.");
        if (req.getEmail() != null && !req.getEmail().isBlank() && userRepo.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already registered.");

        User user = User.builder()
            .name(req.getName().trim())
            .phoneNumber(req.getPhoneNumber())
            .email(req.getEmail() != null ? req.getEmail().trim() : null)
            .passwordHash(passwordEncoder.encode(req.getPassword()))
            .age(req.getAge()).gender(req.getGender().toUpperCase())
            .annualIncome(req.getAnnualIncome())
            .category(req.getCategory().toUpperCase())
            .maritalStatus(req.getMaritalStatus().toUpperCase())
            .village(req.getVillage()).pincode(req.getPincode())
            .district(req.getDistrict()).state(req.getState())
            .role("USER").build();

        User saved = userRepo.save(user);
        log.info("New user registered: {} ({})", saved.getName(), saved.getId());
        return toResponse(saved, "Registration successful");
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByPhoneNumber(req.getPhoneNumber())
            .orElseThrow(() -> new IllegalArgumentException("No account found with this phone number."));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new IllegalArgumentException("Incorrect password.");
        log.info("User logged in: {} ({})", user.getName(), user.getId());
        return toResponse(user, "Login successful");
    }

    public User getUserById(String userId) {
        return userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private AuthResponse toResponse(User u, String msg) {
        return AuthResponse.builder()
            .userId(u.getId()).name(u.getName())
            .phoneNumber(u.getPhoneNumber())
            .pincode(u.getPincode()).state(u.getState())
            .role(u.getRole()).message(msg).build();
    }
}

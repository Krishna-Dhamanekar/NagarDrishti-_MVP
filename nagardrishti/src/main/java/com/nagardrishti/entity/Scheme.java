package com.nagardrishti.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schemes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scheme {

    @Id
    @Column(length = 36)
    private String id;

    // ── Core Details ─────────────────────────────────────────────────────────
    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String benefitAmount; // Stored as String (e.g. "1.5 Lakh", "500/month")

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    private Integer priority;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_categories", joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "category")
    private List<String> schemeCategory; // e.g., ["Education", "Pension"]

    // ── Location Details ─────────────────────────────────────────────────────
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_states", joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "state")
    private List<String> beneficiaryState; // e.g., ["Karnataka", "Central", "All"]

    @Column(length = 100)
    private String beneficiaryDistrict; // For hyper-local scheme matching

    // ── Demographic Limits (Hard Limits) ─────────────────────────────────────
    private Integer minAge;
    private Integer maxAge;

    @Column(length = 10)
    private String gender; // "Male", "Female", "Other", "All"

    private Double maxIncome; // Annual income ceiling

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_eligible_castes", joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "caste_category")
    private List<String> eligibleCategories; // e.g., ["SC", "ST", "OBC", "General", "All"]

    private Double maxLandAllowed; // In Acres

    // ── Socio-Economic Matchers ──────────────────────────────────────────────
    @Column(length = 30)
    private String targetEducationLevel;

    @Column(length = 50)
    private String occupation;

    @Column(length = 20)
    private String maritalStatus;

    // ── Vulnerability & Special Targets (The "Score Boosters") ───────────────
    @Builder.Default private Boolean requiresBpl = false;
    @Builder.Default private Boolean requiresDisability = false;

    @Builder.Default private Boolean targetsWidows = false;
    @Builder.Default private Boolean targetsGirlChild = false;
    @Builder.Default private Boolean targetsSeniorCitizens = false;

    // ── Document Requirements (Hard Limits) ──────────────────────────────────
    @Builder.Default private Boolean requiresRationCard = false;
    @Builder.Default private Boolean requiresBankAccount = false;
    @Builder.Default private Boolean requiresAadhaarLinkedBank = false;

    // ── System ───────────────────────────────────────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void beforePersist() {
        if (id == null) id = java.util.UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Ensure booleans are never null to prevent NullPointerExceptions
        if (active == null) active = true;
        if (requiresBpl == null) requiresBpl = false;
        if (requiresDisability == null) requiresDisability = false;
        if (targetsWidows == null) targetsWidows = false;
        if (targetsGirlChild == null) targetsGirlChild = false;
        if (targetsSeniorCitizens == null) targetsSeniorCitizens = false;
        if (requiresRationCard == null) requiresRationCard = false;
        if (requiresBankAccount == null) requiresBankAccount = false;
        if (requiresAadhaarLinkedBank == null) requiresAadhaarLinkedBank = false;
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
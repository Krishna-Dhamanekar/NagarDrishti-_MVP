package com.nagardrishti.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schemes", indexes = {
        @Index(name = "idx_scheme_active", columnList = "active"),
        @Index(name = "idx_scheme_level", columnList = "level"),
        @Index(name = "idx_scheme_name", columnList = "name")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scheme {

    @Id
    @Column(length = 36)
    private String id;

    @Column(length = 100) // REMOVED unique = true so multiple schemes can share the same slug
    private String slug;

    // ── Core Details ─────────────────────────────────────────────────────────
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String shortTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String level;

    @Column(columnDefinition = "TEXT")
    private String schemeFor;

    @Column(columnDefinition = "TEXT")
    private String benefitAmount;

    @Column(columnDefinition = "TEXT")
    private String benefitType;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    private Integer priority;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_categories", joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "category")
    private List<String> schemeCategory;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_tags", joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "tag")
    private List<String> tags;

    // ── Location Details ─────────────────────────────────────────────────────
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_states", joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "state")
    private List<String> beneficiaryState;

    @Column(columnDefinition = "TEXT")
    private String beneficiaryDistrict;

    // ── Demographic & Eligibility Limits ─────────────────────────────────────
    private Integer minAge;
    private Integer maxAge;

    @Column(columnDefinition = "TEXT")
    private String gender;

    private Double maxIncome;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_eligible_castes", joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "caste_category")
    private List<String> eligibleCategories;

    private Double maxLandAllowed;

    @Column(columnDefinition = "TEXT")
    private String targetEducationLevel;

    @Column(columnDefinition = "TEXT")
    private String occupation;

    @Column(columnDefinition = "TEXT")
    private String maritalStatus;

    // ── Vulnerability Indicators ─────────────────────────────────────────────
    @Builder.Default private Boolean requiresBpl = false;
    @Builder.Default private Boolean requiresDisability = false;
    @Builder.Default private Boolean targetsWidows = false;
    @Builder.Default private Boolean targetsGirlChild = false;
    @Builder.Default private Boolean targetsSeniorCitizens = false;

    // ── Document Requirements ────────────────────────────────────────────────
    @Builder.Default private Boolean requiresRationCard = false;
    @Builder.Default private Boolean requiresBankAccount = false;
    @Builder.Default private Boolean requiresAadhaarLinkedBank = false;

    // ── External Metadata ────────────────────────────────────────────────────
    @Column(columnDefinition = "TEXT")
    private String officialWebsite;

    @Column(columnDefinition = "TEXT")
    private String helpline;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate closeDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void beforePersPersist() {
        if (id == null) id = java.util.UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

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
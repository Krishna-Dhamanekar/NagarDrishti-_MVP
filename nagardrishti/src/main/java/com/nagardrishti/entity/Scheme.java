package com.nagardrishti.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "schemes")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Scheme {

    @Id @Column(length = 50)
    private String id;

    @Column(length = 300)  private String slug;
    @Column(nullable = false, columnDefinition = "TEXT") private String name;
    @Column(length = 300)  private String shortTitle;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(length = 20)   private String level;
    @Column(length = 50)   private String schemeFor;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_categories",
            joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "category")
    @BatchSize(size = 100)
    @Builder.Default
    private List<String> schemeCategory = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_beneficiary_states",
            joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "state")
    @BatchSize(size = 100)
    @Builder.Default
    private List<String> beneficiaryState = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_tags",
            joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "tag")
    @BatchSize(size = 100)
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Column(length = 30)   private String closeDate;
    @Column                private Integer priority;
    @Column(columnDefinition = "TEXT") private String benefitAmount;
    @Column(columnDefinition = "TEXT") private String benefitType;

    // ── Eligibility ────────────────────────────────────────────────────────────
    @Column private Integer minAge;
    @Column private Integer maxAge;
    @Column(length = 10)   private String gender;
    @Column                private Double maxIncome;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_eligible_categories",
            joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "category")
    @BatchSize(size = 100)
    @Builder.Default
    private List<String> eligibleCategories = new ArrayList<>();

    @Column(length = 200)  private String occupation;
    @Column @Builder.Default private Boolean requiresBpl        = false;
    @Column @Builder.Default private Boolean requiresDisability = false;
    @Column                private Double  maxLandAllowed;
    @Column(length = 100)   private String targetEducationLevel;

    // ── Contact ────────────────────────────────────────────────────────────────
    @Column(length = 300)  private String officialWebsite;
    @Column(length = 30)   private String helpline;
    @Column @Builder.Default private Boolean active = true;

    // ── Audit — used for new-scheme notifications ──────────────────────────────
    @Column
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();   // ← NEW

    @PrePersist
    public void beforePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (active    == null) active    = true;
    }
}
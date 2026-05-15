package com.nagardrishti.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Citizen feedback — two types:
 *  PROJECT_COMPLAINT  : citizen reports fake/incomplete/delayed project
 *  SCHEME_VERIFICATION: citizen confirms whether they received scheme money
 */
@Entity @Table(name = "feedback")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Feedback {

    @Id @Column(length = 36) private String id;

    // Who submitted
    @Column(nullable = false, length = 36)
    private String userId;

    @Column(nullable = false, length = 100)
    private String userName;

    // What it's about
    @Column(nullable = false, length = 25)
    private String type;
    // PROJECT_COMPLAINT | SCHEME_VERIFICATION

    @Column(nullable = false, length = 20)
    private String relatedId;       // projectId or schemeId

    @Column(nullable = false, length = 255)
    private String relatedName;     // human-readable name for display

    // Project complaint fields
    @Column(length = 30)
    private String complaintCategory;
    // WORK_NOT_STARTED | POOR_QUALITY | FUNDS_MISUSED |
    // DELAYED | CONTRACTOR_ABSENT | OTHER

    // Scheme verification fields
    @Column private Boolean schemeReceived;   // did you get the money?
    @Column private Double amountReceived;    // how much you actually received
    @Column private Double amountExpected;    // what was promised
    @Column(length = 30)
    private String receivedMonth;             // e.g. "March 2025"

    // Shared
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(length = 500)
    private String photoUrl;        // file path or URL of uploaded proof

    // Moderation
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";
    // PENDING | VERIFIED | REJECTED

    @Column(columnDefinition = "TEXT")
    private String adminRemark;

    // Stats
    @Column @Builder.Default private Integer upvoteCount = 0;

    @Column @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void beforePersist() {
        if (id == null) id = java.util.UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
        if (upvoteCount == null) upvoteCount = 0;
    }
}

package com.nagardrishti.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "projects")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Project {

    @Id @Column(length = 200)
    private String id;

    // ── Core ──────────────────────────────────────────────────────────────────
    @Column(nullable = false, length = 500) private String name;
    @Column(length = 100)  private String category;
    @Column(length = 100)  private String workCategory;       // e.g. "Other Works"
    @Column(columnDefinition = "TEXT") private String description;

    // ── Location ──────────────────────────────────────────────────────────────
    @Column(length = 200)  private String location;
    @Column(length = 10)   private String pincode;            // available in kppp data
    @Column(length = 100)  private String district;
    @Column(length = 100)  private String state;
    @Column(length = 100)  private String ward;
    @Column(length = 100)  private String zone;               // e.g. "Bangalur South"

    // ── Department / Authority ─────────────────────────────────────────────────
    @Column(length = 200)  private String sanctioningAuthority; // full dept name
    @Column(length = 20)   private String departmentCode;       // short code e.g. "BBMP"
    @Column(length = 300)  private String departmentFull;
    @Column(length = 200)  private String contractor;

    // ── Budget ────────────────────────────────────────────────────────────────
    @Column private Double budgetAllocated;    // estimatedValue / sanctioned
    @Column private Double awardedValue;       // contract awarded amount
    @Column private Double budgetSpent;

    // ── Status & Progress ─────────────────────────────────────────────────────
    @Column(length = 20)   private String status;
    // PLANNED | IN_PROGRESS | COMPLETED | DELAYED
    @Column private Integer completionPercentage;
    @Column(length = 50)   private String govtLastUpdated;

    // ── Dates ─────────────────────────────────────────────────────────────────
    @Column(length = 50)   private String tenderPublishedDate;
    @Column(length = 50)   private String contractAwardedDate;
    @Column(length = 50)   private String plannedStartDate;
    @Column(length = 50)   private String plannedCompletionDate;
    @Column(length = 50)   private String expectedCompletionDate;
    @Column(length = 50)   private String actualCompletionDate;
    @Column(length = 50)   private String publishedDate;
    @Column(length = 50)   private String lastBidDate;

    // ── Tender Info ────────────────────────────────────────────────────────────
    @Column(length = 200)  private String tenderNumber;
    @Column(length = 20)   private String tenderType;   // Open | Limited | etc.
    @Column(length = 100)  private String source;       // kppp.karnataka.gov.in

    // ── Links ─────────────────────────────────────────────────────────────────
    @Column(columnDefinition = "TEXT") private String sourceLink;
    @Column(columnDefinition = "TEXT") private String documentUrl;

    // ── Audit ─────────────────────────────────────────────────────────────────
    @Column private LocalDateTime scrapedAt;
    @Column private LocalDateTime createdAt;

    @PrePersist
    public void beforePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
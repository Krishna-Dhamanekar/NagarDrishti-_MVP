package com.nagardrishti.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "users")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id @Column(length = 36) private String id;

    // Core / Auth
    @Column(nullable = false, length = 100)  private String fullName;
    @Column(unique = true, length = 15, nullable = false) private String phoneNumber;
    @Column(unique = true, length = 100)     private String email;
    @Column(nullable = false, length = 100)  private String passwordHash;

    // Personal
    @Column private Integer age;
    @Column(length = 10)  private String gender;

    // Location
    @Column(length = 100) private String state;
    @Column(length = 100) private String district;
    @Column(length = 10, nullable = false) private String pincode;
    @Column(length = 100) private String ward;
    @Column(length = 100) private String zone;    // ← NEW e.g. "Bengaluru South"

    // Socio-Economic
    @Column(length = 20)  private String category;
    @Column               private Double annualIncome;
    @Column @Builder.Default private Boolean bpl = false;
    @Column(length = 30)  private String educationLevel;
    @Column(length = 50)  private String occupation;
    @Column @Builder.Default private Boolean disabled = false;
    @Column               private Integer disabilityPercentage;
    @Column @Builder.Default private Boolean ownsLand = false;
    @Column               private Double landInAcres;

    // Family
    @Column(length = 20)  private String maritalStatus;
    @Column               private Integer familyMembers;
    @Column @Builder.Default private Boolean widow = false;
    @Column @Builder.Default private Boolean seniorCitizenInFamily = false;
    @Column @Builder.Default private Integer girlChildrenCount = 0;

    // Documents
    @Column @Builder.Default private Boolean aadhaarLinked   = false;
    @Column @Builder.Default private Boolean bankAccount     = false;
    @Column @Builder.Default private Boolean rationCard      = false;
    @Column @Builder.Default private Boolean healthInsurance = false;

    // System
    @Column private LocalDateTime registeredDate;
    @Column private LocalDateTime lastLoginAt;

    @Column(length = 20) @Builder.Default private String role = "USER";

    @PrePersist
    public void beforePersist() {
        if (id == null)             id             = java.util.UUID.randomUUID().toString();
        if (registeredDate == null) registeredDate = LocalDateTime.now();
        if (role == null)           role           = "USER";
        if (bpl == null)            bpl            = false;
        if (disabled == null)       disabled       = false;
        if (ownsLand == null)       ownsLand       = false;
        if (widow == null)          widow          = false;
        if (seniorCitizenInFamily == null) seniorCitizenInFamily = false;
        if (girlChildrenCount == null)     girlChildrenCount     = 0;
        if (aadhaarLinked == null)  aadhaarLinked  = false;
        if (bankAccount == null)    bankAccount    = false;
        if (rationCard == null)     rationCard     = false;
        if (healthInsurance == null) healthInsurance = false;
    }
}
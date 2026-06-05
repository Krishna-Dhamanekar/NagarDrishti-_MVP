package com.nagardrishti.dto;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {

    // ── System ───────────────────────────────────────────────────────────────
    private String  userId;
    private String  role;
    private String  message;

    // ── Core ─────────────────────────────────────────────────────────────────
    private String  fullName;
    private String  phoneNumber;
    private String  email;

    // ── Personal ─────────────────────────────────────────────────────────────
    private Integer age;
    private String  gender;

    // ── Location ─────────────────────────────────────────────────────────────
    private String  state;
    private String  district;
    private String  pincode;

    // ── Socio-Economic ───────────────────────────────────────────────────────
    private String  category;
    private Double  annualIncome;
    private Boolean bpl;
    private String  educationLevel;
    private String  occupation;
    private Boolean disabled;
    private Integer disabilityPercentage;
    private Boolean ownsLand;
    private Double  landInAcres;

    // ── Family ───────────────────────────────────────────────────────────────
    private String  maritalStatus;
    private Integer familyMembers;
    private Boolean widow;
    private Boolean seniorCitizenInFamily;
    private Integer girlChildrenCount;

    // ── Documents / Entitlements ──────────────────────────────────────────────
    private Boolean aadhaarLinked;
    private Boolean bankAccount;
    private Boolean rationCard;
    private Boolean healthInsurance;

    // ── Notification — new eligible schemes since last login ──────────────────
    private Integer          newSchemesCount;   // ← NEW: how many new schemes match user
    private List<String>     newSchemeNames;    // ← NEW: names of up to 5 new schemes
}
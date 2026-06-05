package com.nagardrishti.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    // ── Core ─────────────────────────────────────────────────────────────────
    @NotBlank @Size(min = 2, max = 100)
    private String fullName;

    @NotBlank @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
    private String phoneNumber;

    @Email
    private String email;                   // optional

    @NotBlank @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // ── Personal ─────────────────────────────────────────────────────────────
    @NotNull @Min(1) @Max(120)
    private Integer age;

    @NotBlank
    private String gender;                  // MALE | FEMALE | OTHER

    // ── Location ─────────────────────────────────────────────────────────────
    @NotBlank
    private String state;

    private String district;                // optional

    @NotBlank @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    // ── Socio-Economic ───────────────────────────────────────────────────────
    @NotBlank
    private String category;               // GEN | OBC | SC | ST | MINORITY | OTHER

    @NotNull @Min(0)
    private Double annualIncome;

    private Boolean bpl;                   // default false

    @NotBlank
    private String educationLevel;
    // NONE | PRIMARY | SECONDARY | HSC | DIPLOMA | ITI | GRADUATE | POSTGRADUATE | DOCTORATE

    private String occupation;             // optional

    private Boolean disabled;             // default false

    @Min(0) @Max(100)
    private Integer disabilityPercentage; // show only if disabled

    private Boolean ownsLand;            // default false

    @Min(0)
    private Double landInAcres;          // show only if ownsLand

    // ── Family ───────────────────────────────────────────────────────────────
    @NotBlank
    private String maritalStatus;        // UNMARRIED | MARRIED | WIDOW | DIVORCED

    @NotNull @Min(1)
    private Integer familyMembers;

    private Boolean widow;

    private Boolean seniorCitizenInFamily;

    @Min(0)
    private Integer girlChildrenCount;

    // ── Documents / Entitlements ──────────────────────────────────────────────
    private Boolean aadhaarLinked;
    private Boolean bankAccount;
    private Boolean rationCard;
    private Boolean healthInsurance;
}
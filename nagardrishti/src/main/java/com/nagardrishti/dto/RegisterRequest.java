package com.nagardrishti.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    // Core
    @NotBlank @Size(min = 2, max = 100) private String fullName;
    @NotBlank @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
    private String phoneNumber;
    @Email private String email;
    @NotBlank @Size(min = 6) private String password;

    // Personal
    @NotNull @Min(1) @Max(120) private Integer age;
    @NotBlank private String gender;

    // Location
    @NotBlank private String state;
    private String district;
    @NotBlank @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    private String pincode;
    private String ward;
    private String zone;            // ← NEW e.g. "Bengaluru South"

    // Socio-Economic
    @NotBlank private String category;
    @NotNull @Min(0) private Double annualIncome;
    private Boolean bpl;
    @NotBlank private String educationLevel;
    private String occupation;
    private Boolean disabled;
    @Min(0) @Max(100) private Integer disabilityPercentage;
    private Boolean ownsLand;
    @Min(0) private Double landInAcres;

    // Family
    @NotBlank private String maritalStatus;
    @NotNull @Min(1) private Integer familyMembers;
    private Boolean widow;
    private Boolean seniorCitizenInFamily;
    @Min(0) private Integer girlChildrenCount;

    // Documents
    private Boolean aadhaarLinked;
    private Boolean bankAccount;
    private Boolean rationCard;
    private Boolean healthInsurance;
}
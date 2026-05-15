package com.nagardrishti.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Size(min=2, max=100) private String name;
    @NotBlank @Pattern(regexp="^[6-9]\\d{9}$") private String phoneNumber;
    @Email private String email;
    @NotBlank @Size(min=6) private String password;
    @NotNull @Min(1) @Max(120) private Integer age;
    @NotBlank private String gender;
    @NotNull @Min(0) private Double annualIncome;
    @NotBlank private String category;
    @NotBlank private String maritalStatus;
    private String village;
    @NotBlank @Pattern(regexp="^\\d{6}$") private String pincode;
    private String district;
    @NotBlank private String state;
}

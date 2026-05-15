package com.nagardrishti.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "users")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 100) private String name;
    @Column(unique = true, length = 15)     private String phoneNumber;
    @Column(unique = true, length = 100)    private String email;
    @Column(nullable = false, length = 100) private String passwordHash;

    @Column private Integer age;
    @Column(length = 10)  private String gender;
    @Column private Double annualIncome;
    @Column(length = 20)  private String category;
    @Column(length = 20)  private String maritalStatus;

    @Column(length = 100) private String village;
    @Column(length = 10)  private String pincode;
    @Column(length = 100) private String district;
    @Column(length = 100) private String state;

    @Column private LocalDateTime registeredDate;

    @Column(length = 20)
    @Builder.Default
    private String role = "USER";   // USER | ADMIN

    @PrePersist
    public void beforePersist() {
        if (id == null) id = java.util.UUID.randomUUID().toString();
        if (registeredDate == null) registeredDate = LocalDateTime.now();
        if (role == null) role = "USER";
    }
}

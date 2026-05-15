package com.nagardrishti.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "projects")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Project {
    @Id @Column(length = 20) private String id;

    @Column(nullable = false, length = 255) private String name;
    @Column(length = 30)  private String category;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(length = 100) private String location;
    @Column(length = 10)  private String pincode;
    @Column(length = 100) private String district;
    @Column(length = 100) private String state;

    @Column private Double budgetAllocated;
    @Column private Double budgetSpent;
    @Column(length = 20)  private String status;
    @Column(length = 20)  private String startDate;
    @Column(length = 20)  private String expectedCompletionDate;
    @Column(length = 200) private String sanctioningAuthority;
    @Column(length = 200) private String contractor;
    @Column private Integer completionPercentage;
    @Column(length = 500) private String sourceLink;
}

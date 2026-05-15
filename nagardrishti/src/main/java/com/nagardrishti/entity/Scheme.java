package com.nagardrishti.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "schemes")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Scheme {
    @Id @Column(length = 20) private String id;

    @Column(nullable = false, length = 255) private String name;
    @Column(length = 50)  private String shortName;
    @Column(length = 30)  private String category;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(length = 200) private String ministryDepartment;
    @Column(length = 20)  private String scope;
    @Column(length = 100) private String applicableState;

    @Column private Integer minAge;
    @Column private Integer maxAge;
    @Column(length = 10)  private String gender;
    @Column private Double maxAnnualIncome;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_eligible_categories", joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "category")
    @Builder.Default
    private List<String> eligibleCategories = new ArrayList<>();

    @Column(length = 20)  private String maritalStatus;
    @Column private Double benefitAmount;
    @Column(length = 30)  private String benefitType;
    @Column(columnDefinition = "TEXT") private String benefitDescription;
    @Column(columnDefinition = "TEXT") private String howToApply;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scheme_documents", joinColumns = @JoinColumn(name = "scheme_id"))
    @Column(name = "document")
    @Builder.Default
    private List<String> documentsRequired = new ArrayList<>();

    @Column(length = 300) private String officialWebsite;
    @Column(length = 30)  private String helplineNumber;
    @Column(length = 20)  private String launchDate;
    @Column @Builder.Default private Boolean isActive = true;
}

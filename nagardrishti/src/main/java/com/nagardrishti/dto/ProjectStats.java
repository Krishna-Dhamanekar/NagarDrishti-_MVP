package com.nagardrishti.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProjectStats {
    private Integer totalProjects;
    private Double  totalBudgetAllocated;
    private Double  totalBudgetSpent;
    private Integer completedProjects;
    private Integer inProgressProjects;
    private Integer delayedProjects;
    private Integer plannedProjects;
}

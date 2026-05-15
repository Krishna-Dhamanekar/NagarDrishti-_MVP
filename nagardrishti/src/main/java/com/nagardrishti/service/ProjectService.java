package com.nagardrishti.service;

import com.nagardrishti.dto.ProjectStats;
import com.nagardrishti.entity.Project;
import com.nagardrishti.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepo;

    public List<Project> getAllProjects()                       { return projectRepo.findAll(); }
    public List<Project> getByPincode(String p)                { return projectRepo.findByPincode(p); }
    public List<Project> getByVillage(String v)                { return projectRepo.findByLocationIgnoreCase(v); }
    public List<Project> getByDistrict(String d)               { return projectRepo.findByDistrictIgnoreCase(d); }
    public List<Project> getByCategory(String c)               { return projectRepo.findByCategoryIgnoreCase(c); }
    public List<Project> getByStatus(String s)                 { return projectRepo.findByStatusIgnoreCase(s); }

    public Project getById(String id) {
        return projectRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    public ProjectStats getStatsByPincode(String pincode) {
        List<Project> list = projectRepo.findByPincode(pincode);
        return ProjectStats.builder()
            .totalProjects(list.size())
            .totalBudgetAllocated(sum(list, true))
            .totalBudgetSpent(sum(list, false))
            .completedProjects(count(list, "COMPLETED"))
            .inProgressProjects(count(list, "IN_PROGRESS"))
            .delayedProjects(count(list, "DELAYED"))
            .plannedProjects(count(list, "PLANNED"))
            .build();
    }

    private double sum(List<Project> list, boolean allocated) {
        return list.stream().mapToDouble(p ->
            allocated ? (p.getBudgetAllocated() != null ? p.getBudgetAllocated() : 0)
                      : (p.getBudgetSpent()     != null ? p.getBudgetSpent()     : 0)).sum();
    }
    private int count(List<Project> list, String status) {
        return (int) list.stream().filter(p -> status.equalsIgnoreCase(p.getStatus())).count();
    }
}

package com.nagardrishti.service;

import com.nagardrishti.dto.ProjectStats;
import com.nagardrishti.entity.Project;
import com.nagardrishti.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepo;

    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public Project getById(String id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    // ── Standard Filters (Fixes "Cannot resolve method" errors) ───────────────

    public List<Project> getByPincode(String pincode) {
        return projectRepo.findByPincode(pincode);
    }

    public List<Project> getByDistrict(String district) {
        return projectRepo.findByDistrictIgnoreCase(district);
    }

    public List<Project> getByStatus(String status) {
        return projectRepo.findByStatusIgnoreCase(status);
    }

    public List<Project> getByCategory(String category) {
        return projectRepo.findByCategoryIgnoreCase(category);
    }

    public List<Project> getByDepartment(String deptCode) {
        return projectRepo.findByDepartmentCodeIgnoreCase(deptCode);
    }

    public List<Project> getByWard(String ward) {
        return projectRepo.findByWardIgnoreCase(ward);
    }

    public List<Project> getByZone(String zone) {
        return projectRepo.searchByZone(zone.toLowerCase());
    }

    // ── My Area Projects — smart match ────────────────────────────────────────
    public List<Project> getMyAreaProjects(String pincode, String zone) {
        if (pincode != null && !pincode.isBlank() && zone != null && !zone.isBlank()) {
            return projectRepo.findByPincodeOrZoneWhenPincodeNull(pincode, zone);
        } else if (pincode != null && !pincode.isBlank()) {
            return projectRepo.findByPincode(pincode);
        } else if (zone != null && !zone.isBlank()) {
            return projectRepo.findByZoneMatch(zone);
        }
        return new ArrayList<>();
    }

    // ── Notifications ─────────────────────────────────────────────────────────
    public List<Project> getNewInAreaSince(LocalDateTime since, String pincode, String zone) {
        if (since == null) return new ArrayList<>();
        return projectRepo.findNewInArea(since, pincode, zone);
    }

    // ── Analytics / Stats ─────────────────────────────────────────────────────
    public ProjectStats getStats(List<Project> list) {
        if (list == null || list.isEmpty()) {
            return ProjectStats.builder().totalProjects(0).totalBudgetAllocated(0.0).totalBudgetSpent(0.0)
                    .completedProjects(0).inProgressProjects(0).delayedProjects(0).plannedProjects(0).build();
        }

        return ProjectStats.builder()
                .totalProjects(list.size())
                .totalBudgetAllocated(sumAllocated(list))
                .totalBudgetSpent(sumAwarded(list))
                .completedProjects(count(list, "COMPLETED"))
                .inProgressProjects(count(list, "IN_PROGRESS"))
                .delayedProjects(count(list, "DELAYED"))
                .plannedProjects(count(list, "PLANNED"))
                .build();
    }

    public ProjectStats getStatsByPincode(String p) {
        return getStats(getByPincode(p));
    }

    public ProjectStats getStatsByZone(String z) {
        return getStats(getByZone(z));
    }

    public ProjectStats getStatsByArea(String pincode, String zone) {
        return getStats(getMyAreaProjects(pincode, zone));
    }

    public ProjectStats getStatsByDistrict(String d) {
        return getStats(getByDistrict(d));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private double sumAllocated(List<Project> list) {
        return list.stream().mapToDouble(p ->
                p.getBudgetAllocated() != null ? p.getBudgetAllocated() : 0).sum();
    }

    private double sumAwarded(List<Project> list) {
        return list.stream().mapToDouble(p ->
                p.getAwardedValue() != null ? p.getAwardedValue() :
                        p.getBudgetAllocated() != null ? p.getBudgetAllocated() : 0).sum();
    }

    private int count(List<Project> list, String status) {
        return (int) list.stream()
                .filter(p -> status.equalsIgnoreCase(p.getStatus()))
                .count();
    }
}
package com.nagardrishti.service;

import com.nagardrishti.dto.ProjectStats;
import com.nagardrishti.entity.Project;
import com.nagardrishti.entity.User;
import com.nagardrishti.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepo;

    public List<Project> getAllProjects() { return projectRepo.findAll(); }

    public Project getById(String id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    // ── My Area Projects — smart match ────────────────────────────────────────
    /**
     * Match logic:
     *  - If user has pincode AND zone → projects where pincode matches OR
     *    (project pincode is null AND zone matches)
     *  - If user has only pincode → projects by pincode
     *  - If user has only zone → projects by zone
     */
    public List<Project> getMyAreaProjects(String pincode, String zone) {
        if (pincode != null && !pincode.isBlank() && zone != null && !zone.isBlank()) {
            return projectRepo.findByPincodeOrZoneWhenPincodeNull(pincode, zone);
        } else if (pincode != null && !pincode.isBlank()) {
            return projectRepo.findByPincode(pincode);
        } else if (zone != null && !zone.isBlank()) {
            return projectRepo.findByZoneMatch(zone);
        }
        return List.of();
    }

    // ── New project notifications ──────────────────────────────────────────────
    public List<Project> getNewProjectsSince(LocalDateTime since, String pincode, String zone) {
        if (since == null) return List.of();
        if (pincode != null && !pincode.isBlank() && zone != null && !zone.isBlank()) {
            return projectRepo.findNewInArea(since, pincode, zone);
        } else if (pincode != null && !pincode.isBlank()) {
            // fallback: just use zone query with pincode as zone won't match
            return projectRepo.findByPincode(pincode).stream()
                    .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(since))
                    .collect(Collectors.toList());
        } else if (zone != null && !zone.isBlank()) {
            return projectRepo.findNewByZone(since, zone);
        }
        return List.of();
    }

    // ── Browse searches ───────────────────────────────────────────────────────
    public List<Project> getByPincode(String pincode) {
        return projectRepo.findByPincode(pincode);
    }

    public List<Project> getByZone(String zone) {
        List<Project> r = projectRepo.findByZoneIgnoreCase(zone);
        if (r.isEmpty()) r = projectRepo.searchByZone(zone.toLowerCase());
        return r;
    }

    public List<Project> getByDistrict(String d) {
        return projectRepo.findByDistrictIgnoreCase(d);
    }

    public List<Project> getByStatus(String s) {
        return projectRepo.findByStatusIgnoreCase(s);
    }

    public List<Project> getByCategory(String c) {
        return projectRepo.findByCategoryIgnoreCase(c);
    }

    public List<Project> getByDepartment(String dept) {
        List<Project> r = projectRepo.findByDepartmentCodeIgnoreCase(dept);
        if (r.isEmpty()) r = projectRepo.findBySanctioningAuthorityIgnoreCase(dept);
        return r;
    }

    // ── Stats ─────────────────────────────────────────────────────────────────
    public ProjectStats getStats(List<Project> list) {
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
        return getStats(projectRepo.findByPincode(p));
    }

    public ProjectStats getStatsByZone(String z) {
        return getStats(getByZone(z));
    }

    public ProjectStats getStatsByArea(String pincode, String zone) {
        return getStats(getMyAreaProjects(pincode, zone));
    }

    public ProjectStats getStatsByDistrict(String d) {
        return getStats(projectRepo.findByDistrictIgnoreCase(d));
    }

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
                .filter(p -> status.equalsIgnoreCase(p.getStatus())).count();
    }
}
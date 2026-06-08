package com.nagardrishti.controller;

import com.nagardrishti.entity.Project;
import com.nagardrishti.entity.User;
import com.nagardrishti.repository.ProjectRepository;
import com.nagardrishti.service.AuthService;
import com.nagardrishti.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController @RequestMapping("/api/projects")
@CrossOrigin(origins = "*") @RequiredArgsConstructor
public class ProjectController {

    private final ProjectService    projectService;
    private final ProjectRepository projectRepo;
    private final AuthService       authService;

    @GetMapping
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable String id) {
        try { return ResponseEntity.ok(projectService.getById(id)); }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage())); }
    }

    /**
     * GET /api/projects/myarea/{userId}
     * Returns projects matching the user's pincode + zone with smart matching.
     */
    @GetMapping("/myarea/{userId}")
    public ResponseEntity<?> myArea(@PathVariable String userId) {
        try {
            User user = authService.getUserById(userId);
            List<Project> projects = projectService.getMyAreaProjects(
                    user.getPincode(), user.getZone());
            return ResponseEntity.ok(Map.of(
                    "projects", projects,
                    "stats",    projectService.getStats(projects),
                    "pincode",  user.getPincode() != null ? user.getPincode() : "",
                    "zone",     user.getZone()    != null ? user.getZone()    : "",
                    "district", user.getDistrict()!= null ? user.getDistrict(): ""
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/projects/locality
     * Browse search: pincode and/or zone and/or district
     */
    @GetMapping("/locality")
    public ResponseEntity<?> byLocality(
            @RequestParam(required = false) String pincode,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String ward) {
        if (pincode != null && zone != null)
            return ResponseEntity.ok(projectService.getMyAreaProjects(pincode, zone));
        if (pincode  != null) return ResponseEntity.ok(projectService.getByPincode(pincode));
        if (zone     != null) return ResponseEntity.ok(projectService.getByZone(zone));
        if (district != null) return ResponseEntity.ok(projectService.getByDistrict(district));
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    /**
     * GET /api/projects/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> stats(
            @RequestParam(required = false) String pincode,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String district) {
        if (pincode != null && zone != null)
            return ResponseEntity.ok(projectService.getStatsByArea(pincode, zone));
        if (pincode  != null) return ResponseEntity.ok(projectService.getStatsByPincode(pincode));
        if (zone     != null) return ResponseEntity.ok(projectService.getStatsByZone(zone));
        if (district != null) return ResponseEntity.ok(projectService.getStatsByDistrict(district));
        return ResponseEntity.badRequest().body(Map.of("error", "Provide pincode, zone, or district"));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> byStatus(@PathVariable String status) {
        return ResponseEntity.ok(projectService.getByStatus(status));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> byCategory(@PathVariable String category) {
        return ResponseEntity.ok(projectService.getByCategory(category));
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<?> byDepartment(@PathVariable String department) {
        return ResponseEntity.ok(projectService.getByDepartment(department));
    }

    /**
     * POST /api/projects/bulk
     * Accepts kppp/eproc format. Maps fields automatically.
     */
    @PostMapping("/bulk")
    public ResponseEntity<?> bulkImport(@RequestBody List<Map<String, Object>> projects) {
        if (projects == null || projects.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Empty project list"));

        LocalDateTime now = LocalDateTime.now();
        int imported = 0, skipped = 0;

        for (Map<String, Object> raw : projects) {
            try {
                String id = str(raw, "id");
                if (id == null) id = str(raw, "tenderNumber");
                if (id == null) { skipped++; continue; }
                if (projectRepo.existsById(id)) { skipped++; continue; }

                Project p = Project.builder()
                        .id(id)
                        .name(str(raw, "title") != null ? str(raw, "title") : str(raw, "name"))
                        .category(str(raw, "category"))
                        .workCategory(str(raw, "workCategory"))
                        .description(stripHtml(str(raw, "description")))
                        .location(str(raw, "location"))
                        .pincode(pinStr(raw, "pincode"))
                        .district(str(raw, "district"))
                        .state(str(raw, "state"))
                        .ward(str(raw, "ward"))
                        .zone(str(raw, "zone"))
                        .sanctioningAuthority(str(raw, "department"))
                        .departmentCode(str(raw, "departmentCode"))
                        .departmentFull(str(raw, "departmentFull"))
                        .contractor(str(raw, "contractor"))
                        .budgetAllocated(dbl(raw, "estimatedValue") != null
                                ? dbl(raw, "estimatedValue") : dbl(raw, "budgetAllocated"))
                        .awardedValue(dbl(raw, "awardedValue"))
                        .budgetSpent(dbl(raw, "budgetSpent"))
                        .status(mapStatus(str(raw, "status")))
                        .completionPercentage(intVal(raw, "completionPercentage"))
                        .publishedDate(str(raw, "publishedDate"))
                        .lastBidDate(str(raw, "lastBidDate"))
                        .tenderPublishedDate(str(raw,"tenderPublishedDate") != null
                                ? str(raw,"tenderPublishedDate") : str(raw,"publishedDate"))
                        .plannedStartDate(str(raw,"startDate") != null
                                ? str(raw,"startDate") : str(raw,"plannedStartDate"))
                        .plannedCompletionDate(str(raw,"completionDate") != null
                                ? str(raw,"completionDate") : str(raw,"plannedCompletionDate"))
                        .expectedCompletionDate(str(raw,"completionDate") != null
                                ? str(raw,"completionDate") : str(raw,"expectedCompletionDate"))
                        .tenderNumber(str(raw,"tenderNumber") != null ? str(raw,"tenderNumber") : id)
                        .tenderType(str(raw, "tenderType"))
                        .source(str(raw, "source"))
                        .sourceLink(str(raw,"sourceUrl") != null
                                ? str(raw,"sourceUrl") : str(raw,"sourceLink"))
                        .documentUrl(str(raw, "documentUrl"))
                        .createdAt(now)
                        .build();

                projectRepo.save(p);
                imported++;
            } catch (Exception e) { skipped++; }
        }

        return ResponseEntity.ok(Map.of("imported", imported, "skipped", skipped));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String str(Map<String, Object> m, String k) {
        Object v = m.get(k); if (v == null) return null;
        String s = v.toString().trim();
        return s.isBlank() || s.equalsIgnoreCase("null") ? null : s;
    }
    private String pinStr(Map<String, Object> m, String k) {
        Object v = m.get(k); if (v == null) return null;
        String s = v.toString().replaceAll("\\.0$","").trim();
        return s.isBlank() || s.equalsIgnoreCase("null") ? null : s;
    }
    private Double dbl(Map<String, Object> m, String k) {
        Object v = m.get(k); if (v == null) return null;
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return null; }
    }
    private Integer intVal(Map<String, Object> m, String k) {
        Object v = m.get(k); if (v == null) return null;
        try { return (int) Double.parseDouble(v.toString()); } catch (Exception e) { return null; }
    }
    private String stripHtml(String h) {
        if (h == null) return null;
        return h.replaceAll("<[^>]+>"," ").replaceAll("&nbsp;"," ")
                .replaceAll("&amp;","&").replaceAll("\\s+"," ").trim();
    }
    private String mapStatus(String s) {
        if (s == null) return "PLANNED";
        return switch (s.toLowerCase()) {
            case "awarded"   -> "IN_PROGRESS";
            case "closed"    -> "COMPLETED";
            case "cancelled" -> "DELAYED";
            default          -> "PLANNED";
        };
    }
}
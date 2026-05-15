package com.nagardrishti.controller;

import com.nagardrishti.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/projects")
@CrossOrigin(origins="*") @RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<?> all() { return ResponseEntity.ok(projectService.getAllProjects()); }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable String id) {
        try { return ResponseEntity.ok(projectService.getById(id)); }
        catch (IllegalArgumentException e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",e.getMessage())); }
    }

    @GetMapping("/locality")
    public ResponseEntity<?> byLocality(
            @RequestParam(required=false) String pincode,
            @RequestParam(required=false) String village,
            @RequestParam(required=false) String district) {
        if (pincode  != null) return ResponseEntity.ok(projectService.getByPincode(pincode));
        if (village  != null) return ResponseEntity.ok(projectService.getByVillage(village));
        if (district != null) return ResponseEntity.ok(projectService.getByDistrict(district));
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats(@RequestParam String pincode) {
        return ResponseEntity.ok(projectService.getStatsByPincode(pincode));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> byStatus(@PathVariable String status) {
        return ResponseEntity.ok(projectService.getByStatus(status));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> byCategory(@PathVariable String category) {
        return ResponseEntity.ok(projectService.getByCategory(category));
    }
}

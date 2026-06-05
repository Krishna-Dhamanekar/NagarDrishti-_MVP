package com.nagardrishti.controller;

import com.nagardrishti.entity.Scheme;
import com.nagardrishti.repository.SchemeRepository;
import com.nagardrishti.service.SchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/schemes")
@CrossOrigin(origins = "*") @RequiredArgsConstructor
public class SchemeController {

    private final SchemeService    schemeService;
    private final SchemeRepository schemeRepo;

    @GetMapping
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(schemeService.getAllSchemes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable String id) {
        try {
            return ResponseEntity.ok(schemeService.getSchemeById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/eligible/{userId}")
    public ResponseEntity<?> eligible(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(schemeService.getEligibleSchemes(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> byCategory(@PathVariable String category) {
        return ResponseEntity.ok(schemeService.getSchemesByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(required = false, defaultValue = "") String q) {
        return ResponseEntity.ok(schemeService.search(q));
    }

    /**
     * POST /api/schemes/bulk
     * Always stamps createdAt = NOW on each scheme so notification
     * system correctly identifies schemes added after a user's last login.
     */
    @PostMapping("/bulk")
    public ResponseEntity<?> bulkImport(@RequestBody List<Scheme> schemes) {
        if (schemes == null || schemes.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Empty scheme list"));

        LocalDateTime now = LocalDateTime.now();
        schemes.forEach(s -> {
            if (s.getActive()    == null) s.setActive(true);
            if (s.getCreatedAt() == null) s.setCreatedAt(now); // critical for notifications
        });

        List<Scheme> saved = schemeRepo.saveAll(schemes);
        return ResponseEntity.ok(Map.of("imported", saved.size()));
    }
}
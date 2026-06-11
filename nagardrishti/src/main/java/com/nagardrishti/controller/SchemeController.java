package com.nagardrishti.controller;

import com.nagardrishti.dto.EligibleSchemesResponse;
import com.nagardrishti.entity.Scheme;
import com.nagardrishti.entity.User;
import com.nagardrishti.repository.SchemeRepository;
import com.nagardrishti.service.AuthService;
import com.nagardrishti.service.SchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schemes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SchemeController {

    private final SchemeService    schemeService;
    private final SchemeRepository schemeRepository;
    private final AuthService      authService;

    // ── 1. Fetch Eligible Schemes for a User ────────────────────────────────
    @GetMapping("/eligible/{userId}")
    public ResponseEntity<?> getEligibleSchemes(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(schemeService.getEligibleSchemes(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ── 2. Search All Active Schemes ────────────────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<List<Scheme>> searchSchemes(@RequestParam(name = "q", required = false) String q) {
        return ResponseEntity.ok(schemeService.search(q));
    }

    // ── 3. Get Details of a Single Scheme ───────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getSchemeDetails(@PathVariable String id) {
        try {
            Scheme scheme = schemeService.getSchemeById(id);
            return ResponseEntity.ok(scheme);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ── 4. New Schemes Notification Endpoint ───────────────────────────────
    @GetMapping("/eligible/new/{userId}")
    public ResponseEntity<?> getNewSchemes(@PathVariable String userId, @RequestParam LocalDateTime since) {
        // Fetches user object via AuthService to enable eligibility checks
        User user = authService.getUserById(userId);
        return ResponseEntity.ok(schemeService.newEligibleSchemesSince(user, since));
    }

    // ── 5. Bulk Upload Endpoint ─────────────────────────────────────────────
    @PostMapping("/bulk")
    public ResponseEntity<?> createSchemesInBulk(@RequestBody List<Scheme> schemes) {
        if (schemes == null || schemes.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Empty scheme list"));
        }

        // Ensures proper initialization of fields for new schemes
        LocalDateTime now = LocalDateTime.now();
        schemes.forEach(s -> {
            if (s.getActive() == null) s.setActive(true);
            if (s.getCreatedAt() == null) s.setCreatedAt(now);
        });

        List<Scheme> savedSchemes = schemeRepository.saveAll(schemes);
        return ResponseEntity.ok(Map.of("imported", savedSchemes.size()));
    }
}
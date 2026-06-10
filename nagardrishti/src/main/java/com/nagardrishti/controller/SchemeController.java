package com.nagardrishti.controller;

import com.nagardrishti.dto.EligibleSchemesResponse;
import com.nagardrishti.entity.Scheme;
import com.nagardrishti.repository.SchemeRepository;
import com.nagardrishti.service.SchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schemes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SchemeController {

    private final SchemeService schemeService;
    private final SchemeRepository schemeRepository;

    // ── 1. Fetch Eligible Schemes for a User ────────────────────────────────
    @GetMapping("/eligible/{userId}")
    public ResponseEntity<EligibleSchemesResponse> getEligibleSchemes(@PathVariable String userId) {
        EligibleSchemesResponse response = schemeService.getEligibleSchemes(userId);
        return ResponseEntity.ok(response);
    }

    // ── 2. Search All Active Schemes ────────────────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<List<Scheme>> searchSchemes(@RequestParam(name = "q", required = false) String q) {
        List<Scheme> schemes = schemeService.search(q);
        return ResponseEntity.ok(schemes);
    }

    // ── 3. Get Details of a Single Scheme ───────────────────────────────────
    // FIXED: Handled the null condition explicitly to fix both compile errors and IDE warnings
    @GetMapping("/{id}")
    public ResponseEntity<Scheme> getSchemeDetails(@PathVariable String id) {
        Scheme scheme = schemeService.getSchemeById(id);

        if (scheme == null) {
            return ResponseEntity.notFound().build(); // Returns a clean HTTP 404 instead of a blank 200 OK
        }

        return ResponseEntity.ok(scheme);
    }

    // ── 4. Bulk Upload Endpoint (For Database Seeding) ──────────────────────
    @PostMapping("/bulk")
    public ResponseEntity<List<Scheme>> createSchemesInBulk(@RequestBody List<Scheme> schemes) {
        List<Scheme> savedSchemes = schemeRepository.saveAll(schemes);
        return ResponseEntity.ok(savedSchemes);
    }
}
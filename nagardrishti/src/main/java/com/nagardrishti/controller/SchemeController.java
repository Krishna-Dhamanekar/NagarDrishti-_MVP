package com.nagardrishti.controller;

import com.nagardrishti.service.SchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/schemes")
@CrossOrigin(origins="*") @RequiredArgsConstructor
public class SchemeController {

    private final SchemeService schemeService;

    @GetMapping
    public ResponseEntity<?> all() { return ResponseEntity.ok(schemeService.getAllSchemes()); }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable String id) {
        try { return ResponseEntity.ok(schemeService.getSchemeById(id)); }
        catch (IllegalArgumentException e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",e.getMessage())); }
    }

    @GetMapping("/eligible/{userId}")
    public ResponseEntity<?> eligible(@PathVariable String userId) {
        try { return ResponseEntity.ok(schemeService.getEligibleSchemes(userId)); }
        catch (IllegalArgumentException e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",e.getMessage())); }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> byCategory(@PathVariable String category) {
        return ResponseEntity.ok(schemeService.getSchemesByCategory(category));
    }
}

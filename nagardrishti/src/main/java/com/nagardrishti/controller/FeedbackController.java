package com.nagardrishti.controller;

import com.nagardrishti.dto.FeedbackRequest;
import com.nagardrishti.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Feedback REST API
 *
 * POST   /api/feedback                        — submit (registered users only)
 * GET    /api/feedback/related/{relatedId}     — all feedback for a project/scheme (public)
 * GET    /api/feedback/summary/{relatedId}     — summary counts for a card (public)
 * GET    /api/feedback/user/{userId}           — feedback by a user
 * GET    /api/feedback/admin/all              — admin: all feedback
 * GET    /api/feedback/admin/status/{status}  — admin: filter by status
 * GET    /api/feedback/admin/type/{type}      — admin: filter by type
 * PUT    /api/feedback/{id}/status            — admin: verify/reject
 * PUT    /api/feedback/{id}/upvote            — upvote a feedback
 */
@RestController @RequestMapping("/api/feedback")
@CrossOrigin(origins="*") @RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<?> submit(@Valid @RequestBody FeedbackRequest req) {
        try { return ResponseEntity.ok(feedbackService.submit(req)); }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @GetMapping("/related/{relatedId}")
    public ResponseEntity<?> byRelated(@PathVariable String relatedId,
                                       @RequestParam(required=false) String type) {
        if (type != null)
            return ResponseEntity.ok(feedbackService.getByTypeAndRelatedId(type, relatedId));
        return ResponseEntity.ok(feedbackService.getByRelatedId(relatedId));
    }

    @GetMapping("/summary/{relatedId}")
    public ResponseEntity<?> summary(@PathVariable String relatedId) {
        return ResponseEntity.ok(feedbackService.getSummaryForRelated(relatedId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> byUser(@PathVariable String userId) {
        return ResponseEntity.ok(feedbackService.getByUserId(userId));
    }

    // ── Admin endpoints ──────────────────────────────────────────────────────
    @GetMapping("/admin/all")
    public ResponseEntity<?> adminAll() {
        return ResponseEntity.ok(feedbackService.getAllForAdmin());
    }

    @GetMapping("/admin/status/{status}")
    public ResponseEntity<?> adminByStatus(@PathVariable String status) {
        return ResponseEntity.ok(feedbackService.getByStatus(status));
    }

    @GetMapping("/admin/type/{type}")
    public ResponseEntity<?> adminByType(@PathVariable String type) {
        return ResponseEntity.ok(feedbackService.getByType(type));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id,
                                           @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(feedbackService.updateStatus(
                id,
                body.get("status"),
                body.get("adminRemark"),
                body.get("adminUserId")));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/upvote")
    public ResponseEntity<?> upvote(@PathVariable String id) {
        try { return ResponseEntity.ok(feedbackService.upvote(id)); }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
}

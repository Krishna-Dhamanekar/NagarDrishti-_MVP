package com.nagardrishti.service;

import com.nagardrishti.dto.*;
import com.nagardrishti.entity.Feedback;
import com.nagardrishti.entity.User;
import com.nagardrishti.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepo;
    private final AuthService authService;

    /**
     * Submit feedback (only registered users).
     */
    public Feedback submit(FeedbackRequest req) {
        // Validate user exists
        User user = authService.getUserById(req.getUserId());

        Feedback fb = Feedback.builder()
            .userId(user.getId())
            .userName(user.getName())
            .type(req.getType().toUpperCase())
            .relatedId(req.getRelatedId())
            .relatedName(req.getRelatedName())
            .complaintCategory(req.getComplaintCategory())
            .schemeReceived(req.getSchemeReceived())
            .amountReceived(req.getAmountReceived())
            .amountExpected(req.getAmountExpected())
            .receivedMonth(req.getReceivedMonth())
            .description(req.getDescription())
            .photoUrl(req.getPhotoUrl())
            .status("PENDING")
            .upvoteCount(0)
            .build();

        Feedback saved = feedbackRepo.save(fb);
        log.info("Feedback submitted: type={} relatedId={} by user={}",
                 saved.getType(), saved.getRelatedId(), saved.getUserName());
        return saved;
    }

    /**
     * Get feedback for a specific project or scheme — PUBLIC.
     */
    public List<Feedback> getByRelatedId(String relatedId) {
        return feedbackRepo.findByRelatedIdOrderByCreatedAtDesc(relatedId);
    }

    public List<Feedback> getByTypeAndRelatedId(String type, String relatedId) {
        return feedbackRepo.findByTypeAndRelatedIdOrderByCreatedAtDesc(type, relatedId);
    }

    /**
     * Get all feedback submitted by a user.
     */
    public List<Feedback> getByUserId(String userId) {
        return feedbackRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Admin: get all feedback (all statuses).
     */
    public List<Feedback> getAllForAdmin() {
        return feedbackRepo.findAllByOrderByCreatedAtDesc();
    }

    public List<Feedback> getByStatus(String status) {
        return feedbackRepo.findByStatusOrderByCreatedAtDesc(status);
    }

    public List<Feedback> getByType(String type) {
        return feedbackRepo.findByTypeOrderByCreatedAtDesc(type);
    }

    /**
     * Admin: verify or reject a feedback.
     */
    public Feedback updateStatus(String feedbackId, String status, String adminRemark, String adminUserId) {
        // Only ADMIN can do this (check role)
        User admin = authService.getUserById(adminUserId);
        if (!"ADMIN".equalsIgnoreCase(admin.getRole()))
            throw new SecurityException("Only admins can update feedback status.");

        Feedback fb = feedbackRepo.findById(feedbackId)
            .orElseThrow(() -> new IllegalArgumentException("Feedback not found: " + feedbackId));

        fb.setStatus(status.toUpperCase());
        if (adminRemark != null) fb.setAdminRemark(adminRemark);
        Feedback updated = feedbackRepo.save(fb);
        log.info("Feedback {} status updated to {} by admin {}", feedbackId, status, admin.getName());
        return updated;
    }

    /**
     * Upvote a feedback (any logged-in user).
     */
    public Feedback upvote(String feedbackId) {
        Feedback fb = feedbackRepo.findById(feedbackId)
            .orElseThrow(() -> new IllegalArgumentException("Feedback not found: " + feedbackId));
        fb.setUpvoteCount(fb.getUpvoteCount() + 1);
        return feedbackRepo.save(fb);
    }

    /**
     * Summary stats shown publicly on a project/scheme card.
     */
    public FeedbackSummary getSummaryForRelated(String relatedId) {
        List<Feedback> all = feedbackRepo.findByRelatedIdOrderByCreatedAtDesc(relatedId);

        long verified      = all.stream().filter(f -> "VERIFIED".equals(f.getStatus())).count();
        long pending       = all.stream().filter(f -> "PENDING".equals(f.getStatus())).count();
        long complaints    = all.stream().filter(f -> "PROJECT_COMPLAINT".equals(f.getType())).count();
        long verifications = all.stream().filter(f -> "SCHEME_VERIFICATION".equals(f.getType())).count();
        long received      = all.stream().filter(f -> "SCHEME_VERIFICATION".equals(f.getType())
                                                   && Boolean.TRUE.equals(f.getSchemeReceived())).count();
        long notReceived   = all.stream().filter(f -> "SCHEME_VERIFICATION".equals(f.getType())
                                                   && Boolean.FALSE.equals(f.getSchemeReceived())).count();

        return FeedbackSummary.builder()
            .totalFeedback(all.size())
            .verifiedFeedback(verified)
            .pendingFeedback(pending)
            .projectComplaints(complaints)
            .schemeVerifications(verifications)
            .schemeReceivedCount(received)
            .schemeNotReceivedCount(notReceived)
            .build();
    }
}

package com.nagardrishti.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FeedbackSummary {
    private long totalFeedback;
    private long verifiedFeedback;
    private long pendingFeedback;
    private long projectComplaints;
    private long schemeVerifications;
    private long schemeReceivedCount;     // how many confirmed they got money
    private long schemeNotReceivedCount;  // how many said they did NOT get money
}

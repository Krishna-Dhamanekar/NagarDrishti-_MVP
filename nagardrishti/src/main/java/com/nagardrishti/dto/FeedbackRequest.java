package com.nagardrishti.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FeedbackRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Type is required")
    private String type;           // PROJECT_COMPLAINT | SCHEME_VERIFICATION

    @NotBlank(message = "Related ID is required")
    private String relatedId;      // projectId or schemeId

    @NotBlank(message = "Related name is required")
    private String relatedName;

    // Project complaint
    private String complaintCategory;

    // Scheme verification
    private Boolean schemeReceived;
    private Double  amountReceived;
    private Double  amountExpected;
    private String  receivedMonth;

    // Shared
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be 10-1000 characters")
    private String description;

    private String photoUrl;        // optional photo proof
}

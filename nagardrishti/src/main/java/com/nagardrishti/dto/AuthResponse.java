package com.nagardrishti.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String userId;
    private String name;
    private String phoneNumber;
    private String pincode;
    private String state;
    private String role;
    private String message;
}

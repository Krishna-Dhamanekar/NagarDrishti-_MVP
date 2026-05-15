package com.nagardrishti.dto;
import com.nagardrishti.entity.Scheme;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EligibleSchemesResponse {
    private Integer totalEligibleSchemes;
    private Double  totalPotentialBenefit;
    private List<Scheme> schemes;
}

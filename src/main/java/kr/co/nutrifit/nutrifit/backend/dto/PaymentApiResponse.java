package kr.co.nutrifit.nutrifit.backend.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentApiResponse {
    private Long amount;
    private String status;
}

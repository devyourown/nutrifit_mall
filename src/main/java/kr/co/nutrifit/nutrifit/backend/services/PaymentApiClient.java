package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.PaymentApiResponse;

public interface PaymentApiClient {
    PaymentApiResponse getPayment(String paymentId);
}

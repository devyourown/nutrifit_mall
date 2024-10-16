package kr.co.nutrifit.nutrifit.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.nutrifit.nutrifit.backend.dto.PaymentApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class PortOneApiClinet implements PaymentApiClient{

    private final RestTemplate restTemplate;

    @Value("${iamport.apiSecret}")
    private String apiSecret;

    @Override
    public PaymentApiResponse getPayment(String paymentId) {
        try {
            String url = "https://api.portone.io/payments/" + URLEncoder.encode(paymentId, StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "PortOne " + apiSecret);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = response.getBody();;
            if (responseBody == null) {
                throw new IllegalArgumentException("결제 정보가 유효하지 않습니다.");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            Long amount = Long.parseLong(rootNode.path("amount").path("total").asText());
            String status = rootNode.path("status").asText();

            return PaymentApiResponse.builder().amount(amount).status(status).build();
        } catch (Exception e) {
            throw new RuntimeException("결제 정보를 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

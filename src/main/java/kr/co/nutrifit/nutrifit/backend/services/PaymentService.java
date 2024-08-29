package kr.co.nutrifit.nutrifit.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrdererDto;
import kr.co.nutrifit.nutrifit.backend.dto.PaymentDto;
import kr.co.nutrifit.nutrifit.backend.persistence.PaymentRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Payment;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Shipping;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final ShippingService shippingService;
    private final CouponService couponService;
    private final PointService pointService;
    private final ProductService productService;
    private final RestTemplate restTemplate;
    @Value("${iamport.apiKey}")
    private String apiKey;
    @Value("${iamport.apiSecret}")
    private String apiSecret;


    @Transactional
    public void createPayment(User user, PaymentDto paymentDto) {
        try {
            //check Enough Stock
            productService.reduceStock(paymentDto.getCartItems());
            String url = "https://api.portone.io/payments/" + URLEncoder.encode(paymentDto.getOrderId(), StandardCharsets.UTF_8);
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

            validatePaymentAmount(amount, paymentDto.getTotal());
            Payment payment = createPaymentEntity(user, paymentDto, status);
            paymentRepository.save(payment);
            Order order = orderService.createOrder(user, paymentDto.getOrderId(), paymentDto.getCartItems());
            if(paymentDto.getCouponId() != null) {
                couponService.useCoupon(user.getId(), paymentDto.getCouponId(), paymentDto.getTotal());
            }

            if (paymentDto.getUsedPoints() > 0) {
                pointService.usePoints(user, paymentDto.getUsedPoints());
            }

            Shipping shipping = shippingService.createShipping(paymentDto.getOrdererDto(), order);
            order.setShipping(shipping);
            order.setPayment(payment);
            payment.setOrder(order);
            paymentRepository.save(payment);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("결제 처리 중 네트워크 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private void validatePaymentAmount(Long pgAmount, Long amount) {
        if (pgAmount.compareTo(amount) != 0) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }
    }

    private Payment createPaymentEntity(User user, PaymentDto paymentDto, String status) {
        return Payment.builder()
                .id(paymentDto.getOrderId())
                .total(paymentDto.getTotal())
                .shippingFee(paymentDto.getShippingFee())
                .discount(paymentDto.getDiscount())
                .subtotal(paymentDto.getSubtotal())
                .paymentMethod(paymentDto.getPaymentMethod())
                .paymentStatus(status)
                .paymentDate(LocalDateTime.now())
                .usedPoints(paymentDto.getUsedPoints())
                .couponId(paymentDto.getCouponId())
                .user(user)
                .build();
    }

    public PaymentDto getPaymentByIdAndUser(String id, User user) {
        System.out.println(id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 없습니다."));
        if (!payment.getUser().getId().equals(user.getId())) {
            throw new SecurityException("허용되지 않은 접근입니다.");
        }
        return convertToDto(payment);
    }

    public List<PaymentDto> getPaymentsByUser(User user) {
        return paymentRepository.findByUserWithOrdersAndItemsAndShipping(user.getId())
                .stream().map(this::convertToDto)
                .toList();
    }

    public PaymentDto convertToDto(Payment payment) {
        Order order = payment.getOrder();
        Shipping shipping = order.getShipping();
        return PaymentDto.builder()
                .orderId(order.getId())
                .total(payment.getTotal())
                .shippingFee(payment.getShippingFee())
                .discount(payment.getDiscount())
                .subtotal(payment.getSubtotal())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDate(payment.getPaymentDate())
                .cartItems(order.getOrderItems().stream().map(orderItem -> CartItemDto.builder()
                        .id(orderItem.getProduct().getId().toString())
                        .name(orderItem.getProduct().getName())
                        .quantity(orderItem.getQuantity())
                        .imageUrl(orderItem.getProduct().getImageUrls().get(0))
                        .build()).collect(Collectors.toList()))
                .ordererDto(OrdererDto.builder()
                        .recipientName(shipping.getRecipientName())
                        .recipientPhone(shipping.getRecipientPhone())
                        .ordererName(shipping.getOrdererName())
                        .ordererPhone(shipping.getOrdererPhone())
                        .address(shipping.getAddress())
                        .addressDetail(shipping.getAddressDetail())
                        .cautions(shipping.getCautions())
                        .build())
                .couponId(payment.getCouponId())
                .usedPoints(payment.getUsedPoints())
                .build();
    }

}

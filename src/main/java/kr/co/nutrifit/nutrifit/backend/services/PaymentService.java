package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.*;
import kr.co.nutrifit.nutrifit.backend.persistence.PaymentRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Payment;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final CouponService couponService;
    private final PointService pointService;
    private final ProductService productService;
    private final PaymentApiClient paymentApiClient;
    private static final double POINT_MULTIPLIER = 0.05;

    @Transactional
    public void createPayment(User user, PaymentDto paymentDto) {
        try {
            validateCouponAndPoints(paymentDto.getCouponCode(), paymentDto.getUsedPoints(), user, paymentDto.getTotal());
            PaymentApiResponse paymentApiResponse = paymentApiClient.getPayment(paymentDto.getOrderId());
            validatePaymentAmount(paymentApiResponse.getAmount(), paymentDto.getTotal());
            createOrderAndPayment(user, paymentDto, paymentApiResponse.getStatus());
            if (user != null) pointService.addPoints(user, (long) (paymentApiResponse.getAmount() * POINT_MULTIPLIER));
        } catch (Exception e) {
            throw new RuntimeException("결제 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private void validateCouponAndPoints(String couponCode, int points, User user, Long total) {
        if (user == null) return;
        if (couponCode != null) couponService.useCoupon(user, couponCode, total);
        if (points > 0) pointService.usePoints(user, points);
    }

    private void validatePaymentAmount(Long pgAmount, Long amount) {
        if (!pgAmount.equals(amount)) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }
    }

    private void createOrderAndPayment(User user, PaymentDto paymentDto, String status) {
        productService.reduceStock(paymentDto.getOrderItems());
        Order order = orderService.createOrder(
                Optional.ofNullable(user),
                paymentDto.getPhoneNumber(),
                paymentDto.getOrderId(),
                paymentDto.getOrderItems(),
                paymentDto.getOrdererDto()
        );
        Payment payment = createPaymentEntity(user, paymentDto, status, order);
        order.setPayment(payment);
    }

    private Payment createPaymentEntity(User user, PaymentDto paymentDto, String status, Order order) {
        OrdererDto ordererDto = paymentDto.getOrdererDto();
        Payment.PaymentBuilder paymentBuilder = Payment.builder()
                .orderPaymentId(paymentDto.getOrderId())
                .order(order)
                .total(paymentDto.getTotal())
                .shippingFee(paymentDto.getShippingFee())
                .discount(paymentDto.getDiscount())
                .subtotal(paymentDto.getSubtotal())
                .paymentMethod(paymentDto.getPaymentMethod())
                .paymentStatus(status)
                .paymentDate(LocalDateTime.now())
                .recipientName(ordererDto.getRecipientName())
                .recipientPhone(ordererDto.getRecipientPhone())
                .ordererName(ordererDto.getOrdererName())
                .ordererPhone(ordererDto.getOrdererPhone())
                .address(ordererDto.getAddress())
                .addressDetail(ordererDto.getAddressDetail())
                .cautions(ordererDto.getCautions());

        if (user != null) {
            paymentBuilder
                    .user(user)
                    .usedPoints(paymentDto.getUsedPoints())
                    .earnPoints((long) (paymentDto.getTotal() * POINT_MULTIPLIER))
                    .couponCode(paymentDto.getCouponCode());
        }
        return paymentRepository.save(paymentBuilder.build());
    }

    public PaymentDto getPaymentById(String id) {
        List<OrderItemDto> items = orderService.getItemsByPaymentId(id);
        PaymentDto paymentDto = paymentRepository.findByOrderPaymentId(id)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 없습니다."));
        paymentDto.setOrderItems(items);
        return paymentDto;
    }

    public Page<PaymentDto> getPaymentsByUser(Long userId, Pageable pageable) {
        return paymentRepository.findPaymentDtosByUserId(userId, pageable);
    }
}

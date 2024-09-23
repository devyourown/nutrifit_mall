package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrdererDto;
import kr.co.nutrifit.nutrifit.backend.dto.PaymentApiResponse;
import kr.co.nutrifit.nutrifit.backend.dto.PaymentDto;
import kr.co.nutrifit.nutrifit.backend.persistence.PaymentRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Payment;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Shipping;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PaymentApiClient paymentApiClient;


    @Transactional
    public void createPayment(User user, PaymentDto paymentDto) {
        try {
            validateCouponAndPoints(paymentDto.getCouponCode(), paymentDto.getUsedPoints(), user, paymentDto.getTotal());
            PaymentApiResponse paymentApiResponse = paymentApiClient.getPayment(paymentDto.getOrderId());
            validatePaymentAmount(paymentApiResponse.getAmount(), paymentDto.getTotal());
            productService.reduceStock(paymentDto.getOrderItems());
            createOrderAndPaymentAndShipping(user, paymentDto, paymentApiResponse.getStatus());
        } catch (Exception e) {
            throw new RuntimeException("결제 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void createPaymentWithoutUser(PaymentDto paymentDto) {
        try {
            PaymentApiResponse paymentApiResponse = paymentApiClient.getPayment(paymentDto.getOrderId());
            validatePaymentAmount(paymentApiResponse.getAmount(), paymentDto.getTotal());
            productService.reduceStock(paymentDto.getOrderItems());
            createOrderAndPaymentAndShippingWithoutUser(paymentDto, paymentApiResponse.getStatus());
        } catch (Exception e) {
            throw new RuntimeException("결제 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private void validatePaymentAmount(Long pgAmount, Long amount) {
        if (!pgAmount.equals(amount)) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }
    }

    private void createOrderAndPaymentAndShippingWithoutUser(PaymentDto paymentDto, String status) {
        Order order = orderService.createOrderWithoutUser(paymentDto.getPhoneNumber(), paymentDto.getOrderId(), paymentDto.getOrderItems());
        Payment payment = createPaymentEntityWithoutUser(paymentDto, status, order);
        Shipping shipping = shippingService.createShipping(paymentDto.getOrdererDto(), order);
        order.setPayment(payment);
        order.setShipping(shipping);
    }

    private Payment createPaymentEntityWithoutUser(PaymentDto paymentDto, String status, Order order) {
        Payment payment = Payment.builder()
                .orderPaymentId(paymentDto.getOrderId())
                .order(order)
                .total(paymentDto.getTotal())
                .shippingFee(paymentDto.getShippingFee())
                .discount(paymentDto.getDiscount())
                .subtotal(paymentDto.getSubtotal())
                .paymentMethod(paymentDto.getPaymentMethod())
                .paymentStatus(status)
                .paymentDate(LocalDateTime.now())
                .usedPoints(paymentDto.getUsedPoints())
                .couponCode(paymentDto.getCouponCode())
                .build();
        return paymentRepository.save(payment);
    }

    private void createOrderAndPaymentAndShipping(User user, PaymentDto paymentDto, String status) {
        Order order = orderService.createOrder(user, paymentDto.getOrderId(), paymentDto.getOrderItems());
        Payment payment = createPaymentEntity(user, paymentDto, status, order);
        Shipping shipping = shippingService.createShipping(paymentDto.getOrdererDto(), order);
        order.setPayment(payment);
        order.setShipping(shipping);
    }

    private Payment createPaymentEntity(User user, PaymentDto paymentDto, String status, Order order) {
        Payment payment = Payment.builder()
                .orderPaymentId(paymentDto.getOrderId())
                .order(order)
                .total(paymentDto.getTotal())
                .shippingFee(paymentDto.getShippingFee())
                .discount(paymentDto.getDiscount())
                .subtotal(paymentDto.getSubtotal())
                .paymentMethod(paymentDto.getPaymentMethod())
                .paymentStatus(status)
                .paymentDate(LocalDateTime.now())
                .usedPoints(paymentDto.getUsedPoints())
                .couponCode(paymentDto.getCouponCode())
                .user(user)
                .build();
        return paymentRepository.save(payment);
    }

    private void validateCouponAndPoints(String couponCode, int points, User user, Long total) {
        if(couponCode != null) {
            couponService.useCoupon(user, couponCode, total);
        }

        if (points > 0) {
            pointService.usePoints(user, points);
        }
    }

    public PaymentDto getPaymentByIdAndUser(String id) {
        Payment payment = paymentRepository.findByOrderPaymentId(id)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 없습니다."));
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
                .orderId(order.getOrderPaymentId())
                .total(payment.getTotal())
                .shippingFee(payment.getShippingFee())
                .discount(payment.getDiscount())
                .subtotal(payment.getSubtotal())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDate(payment.getPaymentDate())
                .orderItems(order.getOrderItems().stream().map(orderItem -> CartItemDto.builder()
                        .id(orderItem.getProduct().getId())
                        .name(orderItem.getProduct().getName())
                        .quantity(orderItem.getQuantity())
                        .price(orderItem.getPrice())
                        .imageUrl(orderItem.getImageUrl())
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
                .couponCode(payment.getCouponCode())
                .usedPoints(payment.getUsedPoints())
                .build();
    }

}

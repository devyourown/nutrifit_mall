package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.PaymentDto;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.PaymentRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Payment;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.PaymentStatus;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Payment createPayment(User user, PaymentDto paymentDto) {
        Payment payment = Payment.builder()
                .orderId(paymentDto.getOrderId())
                .amount(paymentDto.getAmount())
                .paymentMethod(paymentDto.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .user(user).build();

        Order order = orderRepository.findById(paymentDto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));
        payment.setOrder(order);
        // 결제 성공/실패 로직을 여기에 추가. 포트원에 메시지를 보내서 결제가 실제로 완료됐는지 확인.
        payment.setPaymentStatus(PaymentStatus.SUCCESS); // 성공으로 가정
        return paymentRepository.save(payment);
    }

    public Payment getPaymentByIdAndUser(Long id, User user) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 없습니다."));
        if (!payment.getUser().getId().equals(user.getId())) {
            throw new SecurityException("허용되지 않은 접근입니다.");
        }
        return payment;
    }

    public List<Payment> getPaymentsByUser(User user) {
        return paymentRepository.findByUser(user);
    }
}
